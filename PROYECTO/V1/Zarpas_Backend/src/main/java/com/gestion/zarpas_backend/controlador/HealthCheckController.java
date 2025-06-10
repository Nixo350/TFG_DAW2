package com.gestion.zarpas_backend.controlador; // Asegúrate de que este sea tu paquete base de controladores

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/")
    public String healthCheck() {
        return "OK"; // Una respuesta simple "OK" o "Hello"
    }

    // Opcional: Si quieres un endpoint más específico para salud
    @GetMapping("/health")
    public String customHealth() {
        return "Application is up and running!";
    }
}