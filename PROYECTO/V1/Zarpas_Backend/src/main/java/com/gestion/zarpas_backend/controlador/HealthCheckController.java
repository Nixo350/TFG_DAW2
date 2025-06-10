package com.gestion.zarpas_backend.controlador; // Asegúrate de que este es el paquete correcto donde Spring lo pueda escanear

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/") // Este es el endpoint que Railway intentará contactar
    public String rootEndpoint() {
        return "Zarpas Backend está en línea y funcionando.";
    }

    // Opcional: Si quieres un endpoint /health para un chequeo más específico
    @GetMapping("/health")
    public String customHealthCheck() {
        return "Aplicación Zarpas Backend saludable.";
    }
}