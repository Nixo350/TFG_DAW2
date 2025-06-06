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
import org.springframework.util.AntPathMatcher; // <-- ¡NUEVA IMPORTACIÓN!
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays; // <-- ¡NUEVA IMPORTACIÓN!
import java.util.List; // <-- ¡NUEVA IMPORTACIÓN!

public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    // Lista de rutas que deben ser excluidas de la validación de JWT
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/api/auth/signup",
            "/api/auth/signin",
            // Puedes añadir más rutas públicas aquí si las tienes
            "/api/test/**",
            "/api/usuarios/**", // Si esta ruta tiene endpoints públicos aparte de auth/signup
            "/api/publicaciones/**",
            "/api/comentarios/**"
    );

    // Para la comparación de rutas con patrones Ant-style
    private AntPathMatcher pathMatcher = new AntPathMatcher(); // <-- Inicializa AntPathMatcher

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        // 1. Verificar si la ruta actual está en la lista de rutas excluidas (públicas)
        boolean isPublicPath = EXCLUDED_PATHS.stream()
                .anyMatch(p -> pathMatcher.match(p, requestUri));

        if (isPublicPath) {
            // Si es una ruta pública, simplemente pasa la petición al siguiente filtro
            filterChain.doFilter(request, response);
            return; // ¡IMPORTANTE! Salir del método para no continuar con la validación JWT
        }

        // 2. Si no es una ruta pública, intenta procesar el JWT como lo hacías antes
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null,
                                userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Loguear el error solo si ocurre al intentar validar un JWT,
            // no para rutas públicas sin token
            logger.error("No se pudo establecer la autenticación del usuario: {}", e.getMessage());
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