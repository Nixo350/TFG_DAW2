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
                .authorizeHttpRequests(auth -> auth
                        // 1. Rutas de autenticación y recursos públicos (siempre al principio)
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll() // Para servir imágenes subidas
                        .requestMatchers("/api/test/**").permitAll() // Rutas de prueba
                        .requestMatchers("/api/usuarios/crear").permitAll() // Si el registro de usuarios es público

                        // 2. Rutas de Publicaciones PÚBLICAS (¡TODAS DEBEN IR AQUÍ, ANTES DEL authenticated() GENERAL DE PUBLICACIONES!)
                        .requestMatchers("/api/publicaciones/todas").permitAll() // Ver todas las publicaciones
                        .requestMatchers("/api/publicaciones/buscar").permitAll() // Buscar publicaciones
                        .requestMatchers("/api/publicaciones/{idPublicacion}").permitAll() // Ver una sola publicación
                        .requestMatchers("/api/publicaciones/categoria/{nombreCategoria}").permitAll() // Ver por categoría
                        .requestMatchers("/api/publicaciones/usuario/{idUsuario}").permitAll() // Ver publicaciones de un usuario
                        .requestMatchers(HttpMethod.GET, "/api/publicaciones").permitAll() // Si hay un GET general de publicaciones que quieras que sea público


                        // Rutas de Categorías ESPECÍFICAS (GET de todas las categorías)
                        .requestMatchers(HttpMethod.GET, "/api/publicaciones/categorias/all").permitAll() // ¡ESTA LÍNEA DEBE IR AQUÍ!
                        // Si tienes más GETs públicos específicos dentro de /api/publicaciones, ponlos aquí.

                        // 3. Rutas de comentarios (públicas)
                        .requestMatchers("/api/comentarios/publicacion/**").permitAll() // Ver comentarios de una publicación (público)
                        // ... (resto de reglas de comentarios)


                        // 4. Rutas de Reacciones de Publicaciones (públicas)
                        .requestMatchers("/api/reacciones-publicacion/conteo/**").permitAll() // Conteo de reacciones
                        .requestMatchers("/api/reacciones-publicacion/usuario/**").permitAll() // Si un usuario ha reaccionado (para el frontend)
                        // ... (resto de reglas de reacciones)


                        // 5. Rutas de Reacciones de Comentarios (públicas)
                        .requestMatchers("/api/reacciones-comentario/conteo/**").permitAll()
                        .requestMatchers("/api/reacciones-comentario/usuario/**").permitAll()
                        // ... (resto de reglas de reacciones de comentarios)


                        // TODAS las rutas de /api/publicaciones que *no* fueron explicitamente permitidas antes, ahora requieren autenticación
                        .requestMatchers("/api/publicaciones/**").authenticated() // <-- ¡AHORA ESTA REGLA VA AQUÍ, DESPUÉS DE LOS PERMITALLS ESPECÍFICOS!


                        // Rutas de Categorías que requieren autenticación (POST, PUT, DELETE)
                        .requestMatchers(HttpMethod.POST, "/api/publicaciones/categorias").authenticated() // Solo usuarios autenticados pueden crear categorías
                        .requestMatchers(HttpMethod.PUT, "/api/publicaciones/categorias/**").authenticated() // Solo usuarios autenticados pueden actualizar categorías
                        .requestMatchers(HttpMethod.DELETE, "/api/publicaciones/categorias/**").authenticated() // Solo usuarios autenticados pueden eliminar categorías

                        .requestMatchers(HttpMethod.POST, "/api/publicaciones/categoria").authenticated() // Solo usuarios autenticados pueden crear categorías
                        .requestMatchers(HttpMethod.PUT, "/api/publicaciones/categoria/**").authenticated() // Solo usuarios autenticados pueden actualizar categorías
                        .requestMatchers(HttpMethod.DELETE, "/api/publicaciones/categoria/**").authenticated() // Solo usuarios autenticados pueden eliminar categorías


                        // 6. Rutas de Usuarios (requieren autenticación)
                        .requestMatchers("/api/usuarios/{id}").authenticated() // Ver usuario individual
                        .requestMatchers("/api/usuarios/**").authenticated() // El resto de /api/usuarios (ej: actualizar perfil, borrar)

                        // 7. Cualquier otra petición no especificada requiere autenticación por defecto
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}