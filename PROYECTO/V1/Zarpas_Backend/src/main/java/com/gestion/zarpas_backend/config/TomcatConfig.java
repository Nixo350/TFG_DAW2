package com.gestion.zarpas_backend.config; // Asegúrate de que el paquete sea el correcto

import org.apache.catalina.startup.Catalina;
import org.apache.catalina.startup.Tomcat;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.catalina.core.AprLifecycleListener; // Importa esta clase

@Configuration
public class TomcatConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return (factory) -> {
            factory.addContextCustomizers(context -> {
                // Eliminar explícitamente el AprLifecycleListener
                // Esto debería evitar que Tomcat intente cargar las librerías nativas de APR
                context.removeLifecycleListener(new AprLifecycleListener());
            });

            // También puedes intentar forzar el protocolo NIO aquí si el anterior no falló
            // aunque el error que mencionas hace que esta línea no funcione
            // factory.addConnectorCustomizers(connector -> {
            //    connector.setProtocol("org.apache.coyote.http11.Http11NioProtocol");
            // });
        };
    }
}