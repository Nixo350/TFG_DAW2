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
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    // Lista de rutas que deben ser excluidas de la validación de JWT
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/api/auth/**",
            "/api/publicaciones/**" // ¡AÑADIDA ESTA LÍNEA!
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher(); // Necesario para comparar rutas con patrones

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        String httpMethod = request.getMethod();

        logger.info("AuthTokenFilter: Interceptando solicitud para la URL: {}", requestUri);
        logger.info("AuthTokenFilter: Método HTTP: {}", httpMethod);

        // 1. Comprueba si la ruta es pública y debe ser excluida de la validación JWT
        boolean isPublicPath = EXCLUDED_PATHS.stream().anyMatch(p -> pathMatcher.match(p, requestUri));

        if (isPublicPath) {
            logger.info("AuthTokenFilter: Ruta '{}' es pública. Saltando validación JWT.", requestUri);
            filterChain.doFilter(request, response); // Continúa la cadena de filtros sin validar JWT
            return; // Importante para detener la ejecución aquí
        }

        logger.info("AuthTokenFilter: Ruta '{}' NO es pública. Intentando validar JWT.", requestUri);

        // 2. Si no es una ruta pública, intenta procesar el JWT como lo hacías antes
        try {
            String jwt = parseJwt(request);
            if (jwt != null) {
                logger.info("AuthTokenFilter: Encabezado Authorization: {}", request.getHeader("Authorization"));
                if (jwt.startsWith("Bearer ")) {
                    logger.info("AuthTokenFilter: Prefijo 'Bearer ' encontrado. JWT extraído.");
                    logger.info("AuthTokenFilter: Token JWT extraído: {}...", jwt.substring(0, Math.min(jwt.length(), 20))); // Log limitado
                }
                if (jwtUtils.validateJwtToken(jwt)) {
                    String username = jwtUtils.getUserNameFromJwtToken(jwt);
                    logger.info("AuthTokenFilter: Username extraído del token: {}", username);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails,
                                    null,
                                    userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("AuthTokenFilter: Autenticación establecida en SecurityContextHolder para: {}", username);
                }
            } else {
                logger.info("AuthTokenFilter: No se encontró token JWT en la solicitud.");
            }
        } catch (Exception e) {
            logger.error("No se pudo establecer la autenticación del usuario: {}", e.getMessage(), e);
            // Considera enviar un error 401 si no se puede establecer la autenticación para una ruta NO pública
            // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            // return; // Detener la cadena de filtros aquí si envías un error
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}