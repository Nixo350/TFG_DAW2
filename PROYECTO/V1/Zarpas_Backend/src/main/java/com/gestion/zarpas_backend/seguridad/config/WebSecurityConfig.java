package com.gestion.zarpas_backend.seguridad.config;

import com.gestion.zarpas_backend.seguridad.jwt.AuthEntryPointJwt;
import com.gestion.zarpas_backend.seguridad.jwt.AuthTokenFilter;
import com.gestion.zarpas_backend.servicio.impl.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration; // Importa CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // Importa UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter; // Importa CorsFilter
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays; // Importa Arrays

@Configuration
@EnableMethodSecurity
@EnableWebSecurity(debug = true)
public class WebSecurityConfig implements WebMvcConfigurer {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

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
        // Configura el manejador de recursos para servir las imágenes subidas.
        // "/uploads/**" es el patrón de URL que el frontend usará para solicitar las imágenes.
        // "file:./uploads/" es la ubicación física en el sistema de archivos del servidor
        // donde Spring Boot buscará esas imágenes.
        // Asegúrate de que esta ruta (file:./uploads/) coincida con la ruta que has configurado
        // en tu StorageService (this.rootLocation = Paths.get("uploads")).
        // Si en StorageService usaras, por ejemplo, Paths.get("/ruta/absoluta/a/imagenes"),
        // entonces aquí deberías poner .addResourceLocations("file:/ruta/absoluta/a/imagenes/").
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
                .authorizeHttpRequests(auth ->
                        // 1. Permite acceso público a todas las publicaciones PRIMERO
                        auth.requestMatchers("/api/publicaciones/**").permitAll() // <--- ¡Mueve esta regla al principio!

                                // 2. Luego, las rutas de autenticación
                                .requestMatchers("/api/auth/**").permitAll() // Cubre signup, signin y cualquier otra sub-ruta

                                // 3. Rutas de prueba también públicas
                                .requestMatchers("/api/test/**").permitAll()
                                // Ruta publica a imagenes
                                .requestMatchers("/uploads/**").permitAll()
                                // 4. Otras rutas que quieras hacer públicas (si es el caso)
                                .requestMatchers("/api/comentarios/**").permitAll()
                                .requestMatchers("/api/usuarios/**").permitAll()

                                //NUEVO
                                .requestMatchers("/api/comentarios/publicacion/**").permitAll() // Permite ver comentarios de una publicación
                                .requestMatchers("/api/comentarios/**").authenticated() // Crear, actualizar, eliminar comentario requiere autenticación

                                // --- ¡NUEVO! Rutas de Reacciones de Comentarios ---
                                .requestMatchers("/api/reacciones-comentario/conteo/**").permitAll() // Permite ver conteos de comentarios
                                .requestMatchers("/api/reacciones-comentario/usuario/**").permitAll() // Permite ver la reacción de un usuario a un comentario
                                .requestMatchers("/api/reacciones-comentario/**").authenticated() // El resto requiere autenticación (toggle)


                                .requestMatchers("/api/reacciones-publicacion/conteo/**").permitAll() // Permite ver conteos
                                .requestMatchers("/api/reacciones-publicacion/usuario/**").permitAll() // Permite ver la reaccion de un usuario (si te sirve para uncheckead)
                                .requestMatchers("/api/reacciones-publicacion/**").authenticated()

                                // 5. Cualquier otra petición requiere autenticación al final
                                .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}