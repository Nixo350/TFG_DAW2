package com.gestion.zarpas_backend.seguridad.jwt;

import com.gestion.zarpas_backend.servicio.impl.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken; // Importar para autenticación anónima
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils; // Importar para AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.http.HttpMethod; // Importar HttpMethod para usar en isPublicRoute

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    // Define las rutas que son públicamente accesibles (no requieren JWT) para métodos GET
    private static final Set<String> PUBLIC_GET_PATHS = new HashSet<>(Arrays.asList(
            "/api/publicaciones/todas",
            "/api/publicaciones/buscar", // Asumo que /api/publicaciones/buscar puede tener subrutas o parámetros
            "/api/publicaciones/categoria/", // Ej: /api/publicaciones/categoria/perro
            "/api/publicaciones/usuario/",   // Ej: /api/publicaciones/usuario/123
            "/api/publicaciones/",           // Si tienes un GET general /api/publicaciones que sea público y no tengas subrutas autenticadas con GET
            "/api/publicaciones/categorias/all", // Obtener todas las categorías (GET)
            "/api/publicaciones/reacciones/conteo/",
            "/api/comentarios/publicacion/", // Ver comentarios de una publicación (GET)
            "/api/reacciones-publicacion/conteo/", // Conteo de reacciones (GET)
            "/api/reacciones-publicacion/publicacion/", // <--- ¡Esta es la línea clave añadida! Coincidirá con /api/reacciones-publicacion/publicacion/{id} y /publicacion/{id}/usuario/{id}
            "/api/reacciones-publicacion/usuario/", // Si un usuario ha reaccionado (GET)
            "/api/reacciones-comentario/conteo/", // Conteo de reacciones a comentarios (GET)
            "/api/reacciones-comentario/usuario/"  // Si un usuario ha reaccionado a un comentario (GET)
    ));

    // Para POST, usaremos equals para coincidencia exacta de rutas públicas.
    private static final Set<String> PUBLIC_POST_PATHS = new HashSet<>(Arrays.asList(
            "/api/auth/signin", // Iniciar sesión
            "/api/auth/signup", // Registrarse
            "/api/usuarios/crear" // Si el registro de usuarios es público
            // *** ASEGÚRATE de que NUNCA esté aquí "/api/publicaciones/crear-con-imagen" u otras rutas de POST autenticadas ***
    ));

    // Método para determinar si una ruta es pública según el método HTTP
    private boolean isPublicRoute(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Manejar rutas GET públicas
        if (HttpMethod.GET.matches(method)) { // Usa HttpMethod para una comparación robusta
            for (String publicPath : PUBLIC_GET_PATHS) {
                // Usamos startsWith porque las rutas GET pueden tener IDs o parámetros
                if (path.startsWith(publicPath)) {
                    logger.info("AuthTokenFilter: Ruta '{}' (GET) es pública. Saltando validación JWT.", path);
                    return true;
                }
            }
        }
        // Manejar rutas POST públicas (coincidencia exacta)
        else if (HttpMethod.POST.matches(method)) { // Usa HttpMethod
            for (String publicPath : PUBLIC_POST_PATHS) {
                // Para POSTs públicos, normalmente buscamos coincidencias exactas
                if (path.equals(publicPath)) {
                    logger.info("AuthTokenFilter: Ruta '{}' (POST) es pública. Saltando validación JWT.", path);
                    return true;
                }
            }
        }
        // Manejar rutas de recursos estáticos (ej. imágenes subidas)
        // Normalmente son GET requests
        if (path.startsWith("/uploads/") && HttpMethod.GET.matches(method)) {
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
                // Si la ruta es pública, no necesitamos un token JWT.
                // Es buena práctica establecer un Authentication para el usuario anónimo
                // para que los filtros posteriores de Spring Security no denieguen el acceso
                // por falta de autenticación.
                SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken("key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));
                logger.debug("AuthTokenFilter: La ruta '{}' es pública. Continuando con el filtro.", requestUrl);
                filterChain.doFilter(request, response);
                return; // Es crucial salir temprano para rutas públicas para evitar la validación de JWT
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
                // AuthEntryPointJwt (que es el AuthenticationEntryPoint configurado en WebSecurityConfig)
                // será invocado por Spring Security más adelante en la cadena de filtros
                // para enviar la respuesta 401.
                logger.warn("AuthTokenFilter: No se encontró token JWT o es inválido para la ruta no pública: {}", requestUrl);
            }
        } catch (Exception e) {
            // Captura cualquier excepción durante el proceso del filtro (ej. problemas con el JWT)
            logger.error("AuthTokenFilter: No se pudo establecer la autenticación del usuario para {}: {}", requestUrl, e.getMessage());
            // No lanzar la excepción aquí, dejar que Spring Security la maneje o el AuthEntryPointJwt.
        }

        // Continúa con la cadena de filtros. Si la autenticación falló (jwt==null o inválido),
        // Spring Security lo detectará y el AuthEntryPointJwt se encargará del 401.
        filterChain.doFilter(request, response);
    }

    // Método para extraer el token JWT de la cabecera "Authorization"
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // Extrae el token después de "Bearer "
        }

        return null;
    }
}