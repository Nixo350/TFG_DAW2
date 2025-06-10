package com.gestion.zarpas_backend.seguridad.config;

import com.gestion.zarpas_backend.seguridad.jwt.AuthEntryPointJwt;
import com.gestion.zarpas_backend.seguridad.jwt.AuthTokenFilter;
import com.gestion.zarpas_backend.servicio.impl.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity(debug = true)
public class WebSecurityConfig implements WebMvcConfigurer {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    private AuthTokenFilter authTokenFilter;

    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);



    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        logger.info("Configurando SecurityFilterChain: permitiendo acceso público a /api/publicaciones/**");

        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
                    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(Arrays.asList("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ***** 1. RUTAS PÚBLICAS Y HEALTHCHECK (PONLAS AL PRINCIPIO) *****
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/health").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/api/test/**").permitAll()
                        .requestMatchers("/api/usuarios/crear").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/favicon.ico").permitAll()

                        // Rutas de Publicaciones PÚBLICAS
                        .requestMatchers("/api/publicaciones/todas").permitAll()
                        .requestMatchers("/api/publicaciones/buscar").permitAll()
                        .requestMatchers("/api/publicaciones/{idPublicacion}").permitAll()
                        .requestMatchers("/api/publicaciones/categoria/{nombreCategoria}").permitAll()
                        .requestMatchers("/api/publicaciones/usuario/{idUsuario}").permitAll()
                        .requestMatchers("/api/publicaciones/usuario/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/publicaciones").permitAll()

                        // Rutas de Categorías ESPECÍFICAS
                        .requestMatchers(HttpMethod.GET, "/api/publicaciones/categorias/all").permitAll()

                        // Rutas de comentarios (públicas)
                        .requestMatchers("/api/comentarios/publicacion/**").permitAll()

                        // Rutas de Reacciones de Publicaciones (públicas)
                        .requestMatchers("/api/reacciones-publicacion/conteo/**").permitAll()
                        .requestMatchers("/api/publicaciones/reacciones/conteo/**").permitAll()
                        .requestMatchers("/api/reacciones-publicacion/usuario/**").permitAll()
                        .requestMatchers("/api/reacciones-publicacion/publicacion/**").permitAll()

                        // Rutas de Reacciones de Comentarios (públicas)
                        .requestMatchers("/api/reacciones-comentario/conteo/**").permitAll()
                        .requestMatchers("/api/reacciones-comentario/usuario/**").permitAll()

                        // ***** 2. RUTAS PROTEGIDAS (después de las públicas) *****
                        // Todas las rutas de /api/publicaciones (incluyendo las GET que ya permitiste antes)
                        // ESTA LÍNEA DEBE IR DESPUÉS DE LAS PERMITIDAS ESPECÍFICAS DE /api/publicaciones GET
                        .requestMatchers("/api/publicaciones/**").authenticated() // Asegúrate de que esta no sea muy restrictiva

                        // Rutas de Categorías que requieren autenticación (POST, PUT, DELETE)
                        .requestMatchers(HttpMethod.POST, "/api/publicaciones/categorias").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/publicaciones/categorias/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/publicaciones/categorias/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/publicaciones/categoria").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/publicaciones/categoria/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/publicaciones/categoria/**").authenticated()


                        // Rutas de Usuarios (requieren autenticación)
                        .requestMatchers("/api/usuarios/{id}").authenticated()
                        .requestMatchers("/api/usuarios/**").authenticated()


                        // ***** 3. CUALQUIER OTRA COSA POR DEFECTO REQUIERE AUTENTICACIÓN *****
                        // Esta debe ser la ÚLTIMA regla
                        .anyRequest().authenticated()

                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}