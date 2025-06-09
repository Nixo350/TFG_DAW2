package com.gestion.zarpas_backend.seguridad.jwt;

import com.gestion.zarpas_backend.servicio.impl.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.http.HttpMethod;

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

    private static final Set<String> PUBLIC_GET_PATHS = new HashSet<>(Arrays.asList(
            "/api/publicaciones/todas",
            "/api/publicaciones/buscar",
            "/api/publicaciones/categoria/",
            "/api/publicaciones/usuario/",
            "/api/publicaciones/",
            "/api/publicaciones/categorias/all",
            "/api/publicaciones/reacciones/conteo/",
            "/api/comentarios/publicacion/",
            "/api/reacciones-publicacion/conteo/",
            "/api/reacciones-publicacion/publicacion/",
            "/api/reacciones-publicacion/usuario/",
            "/api/reacciones-comentario/conteo/",
            "/api/reacciones-comentario/usuario/"
    ));

    // Para POST, usaremos equals para coincidencia exacta de rutas públicas.
    private static final Set<String> PUBLIC_POST_PATHS = new HashSet<>(Arrays.asList(
            "/api/auth/signin",
            "/api/auth/signup",
            "/api/usuarios/crear"
    ));

    // Método para determinar si una ruta es pública según el método HTTP
    private boolean isPublicRoute(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (HttpMethod.GET.matches(method)) {
            for (String publicPath : PUBLIC_GET_PATHS) {
                if (path.startsWith(publicPath)) {
                    logger.info("AuthTokenFilter: Ruta '{}' (GET) es pública. Saltando validación JWT.", path);
                    return true;
                }
            }
        }
        else if (HttpMethod.POST.matches(method)) {
            for (String publicPath : PUBLIC_POST_PATHS) {
                if (path.equals(publicPath)) {
                    logger.info("AuthTokenFilter: Ruta '{}' (POST) es pública. Saltando validación JWT.", path);
                    return true;
                }
            }
        }
        if (path.startsWith("/uploads/") && HttpMethod.GET.matches(method)) {
            logger.info("AuthTokenFilter: Ruta '{}' (recurso estático) es pública. Saltando validación JWT.", path);
            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestUrl = request.getRequestURI();
        String requestMethod = request.getMethod();

        logger.info("AuthTokenFilter: Interceptando solicitud para la URL: {} con método: {}", requestUrl, requestMethod);

        try {
            if (isPublicRoute(request)) {
                SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken("key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));
                logger.debug("AuthTokenFilter: La ruta '{}' es pública. Continuando con el filtro.", requestUrl);
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("AuthTokenFilter: Token JWT válido establecido para el usuario: {}", username);
            } else {
                logger.warn("AuthTokenFilter: No se encontró token JWT o es inválido para la ruta no pública: {}", requestUrl);
            }
        } catch (Exception e) {
            logger.error("AuthTokenFilter: No se pudo establecer la autenticación del usuario para {}: {}", requestUrl, e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    // Método para extraer el token JWT de la cabecera "Authorization"
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}