package com.gestion.zarpas_backend.seguridad.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

// Los otros imports como AuthEntryPointJwt, AuthTokenFilter, UserDetailsServiceImpl, etc.,
// los puedes mantener, pero sus @Autowired no se usarán si comentas los beans/filtros.

@Configuration
@EnableMethodSecurity
@EnableWebSecurity(debug = true)
public class WebSecurityConfig { // Quité `implements WebMvcConfigurer` temporalmente, no es relevante para esta prueba

    // Comenta o quita los @Autowired de tus servicios de seguridad y filtros
    // @Autowired UserDetailsServiceImpl userDetailsService;
    // @Autowired private AuthEntryPointJwt unauthorizedHandler;
    // @Autowired private AuthTokenFilter authTokenFilter;

    // private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class); // Puedes comentar el logger también

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // logger.info("Configurando SecurityFilterChain: TODO PERMITIDO PARA DEBUGGING EXTREMO"); // Comenta el log

        http.csrf(csrf -> csrf.disable()) // Mantén el CSRF deshabilitado si lo necesitas
                .cors(cors -> cors.disable()) // O desactiva CORS temporalmente si tienes problemas
                // Comenta todas las líneas de manejo de excepciones y sesión para esta prueba
                // .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                // .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                                // *******************************************************************
                                // LA ÚNICA REGLA ACTIVA: PERMITIR CUALQUIER REQUERIMIENTO
                                .anyRequest().permitAll() // ¡¡ESTO DESHABILITA LA SEGURIDAD COMPLETAMENTE!!
                        // *******************************************************************
                );

        // Comenta estas líneas para que no se añadan los filtros de seguridad avanzados
        // http.authenticationProvider(authenticationProvider());
        // http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Comenta todos los otros @Bean de seguridad (authenticationProvider, authenticationManager, passwordEncoder)
    // También comenta o elimina addResourceHandlers
}