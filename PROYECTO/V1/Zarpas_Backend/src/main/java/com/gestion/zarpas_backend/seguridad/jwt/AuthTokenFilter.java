package com.gestion.zarpas_backend.seguridad.jwt;

import com.gestion.zarpas_backend.servicio.impl.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    // Define las rutas que son públicamente accesibles (no requieren JWT)
    private static final Set<String> PUBLIC_GET_PATHS = new HashSet<>(Arrays.asList(
            "/api/publicaciones/todas",
            "/api/publicaciones/buscar",
            "/api/publicaciones/categoria/", // Ej: /api/publicaciones/categoria/perro
            "/api/publicaciones/usuario/",   // Ej: /api/publicaciones/usuario/123
            "/api/publicaciones/",           // Si tienes un GET general /api/publicaciones que sea público
            "/api/publicaciones/categorias/all", // Obtener todas las categorías (GET)
            "/api/comentarios/publicacion/", // Ver comentarios de una publicación (GET)
            "/api/reacciones-publicacion/conteo/", // Conteo de reacciones (GET)
            "/api/reacciones-publicacion/usuario/", // Si un usuario ha reaccionado (GET)
            "/api/reacciones-comentario/conteo/", // Conteo de reacciones a comentarios (GET)
            "/api/reacciones-comentario/usuario/"  // Si un usuario ha reaccionado a un comentario (GET)
    ));

    // Para POST, usaremos equals para coincidencia exacta de rutas públicas.
    private static final Set<String> PUBLIC_POST_PATHS = new HashSet<>(Arrays.asList(
            "/api/auth/signin", // Iniciar sesión
            "/api/auth/signup", // Registrarse
            "/api/usuarios/crear" // Si el registro de usuarios es público
            // *** ASEGÚRATE de que NUNCA esté aquí "/api/publicaciones/crear-con-imagen" ***
            // Queremos que esta ruta POST SÍ requiera autenticación.
    ));

    private boolean isPublicRoute(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Manejar rutas GET públicas
        if (method.equals("GET")) {
            for (String publicPath : PUBLIC_GET_PATHS) {
                // Usamos startsWith porque las rutas GET pueden tener IDs o parámetros
                if (path.startsWith(publicPath)) {
                    logger.info("AuthTokenFilter: Ruta '{}' (GET) es pública. Saltando validación JWT.", path);
                    return true;
                }
            }
        }
        // Manejar rutas POST públicas (coincidencia exacta)
        else if (method.equals("POST")) {
            for (String publicPath : PUBLIC_POST_PATHS) {
                // Para POSTs públicos, normalmente buscamos coincidencias exactas
                if (path.equals(publicPath)) {
                    logger.info("AuthTokenFilter: Ruta '{}' (POST) es pública. Saltando validación JWT.", path);
                    return true;
                }
            }
        }
        // Manejar rutas de recursos estáticos (ej. imágenes subidas)
        if (path.startsWith("/uploads/") && method.equals("GET")) {
            logger.info("AuthTokenFilter: Ruta '{}' (recurso estático) es pública. Saltando validación JWT.", path);
            return true;
        }

        return false; // Por defecto, la ruta no es pública y requerirá validación JWT
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestUrl = request.getRequestURI();
        String requestMethod = request.getMethod();

        logger.info("AuthTokenFilter: Interceptando solicitud para la URL: {} con método: {}", requestUrl, requestMethod);

        try {
            // 1. Verificar si la ruta es pública. Si lo es, permite el paso sin validar JWT.
            if (isPublicRoute(request)) {
                logger.debug("AuthTokenFilter: La ruta '{}' es pública. Continuando con el filtro.", requestUrl);
                filterChain.doFilter(request, response);
                return; // Es crucial salir temprano para rutas públicas
            }

            // 2. Si la ruta NO es pública, intenta parsear y validar el JWT.
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null, // No guardamos las credenciales aquí
                                userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("AuthTokenFilter: Token JWT válido establecido para el usuario: {}", username);
            } else {
                // Si la ruta no es pública pero no hay JWT o es inválido,
                // Spring Security lo manejará (ej. lanzando una excepción de autenticación
                // que será capturada por AuthEntryPointJwt).
                logger.warn("AuthTokenFilter: No se encontró token JWT o es inválido para la ruta no pública: {}", requestUrl);
            }
        } catch (Exception e) {
            logger.error("AuthTokenFilter: No se pudo establecer la autenticación del usuario para {}: {}", requestUrl, e.getMessage());
            // No lanzar la excepción aquí, Spring Security la manejará más adelante si la autenticación falla
        }

        // Continúa con la cadena de filtros, incluso si la autenticación falló,
        // para que otros filtros de seguridad (como AuthorizationFilter) puedan actuar.
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // Extrae el token después de "Bearer "
        }

        return null;
    }
}