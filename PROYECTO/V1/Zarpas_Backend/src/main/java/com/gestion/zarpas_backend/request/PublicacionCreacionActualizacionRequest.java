// src/main/java/com/gestion/zarpas_backend/dto/PublicacionCreacionActualizacionRequest.java
package com.gestion.zarpas_backend.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set; // Importar Set

@Data
public class PublicacionCreacionActualizacionRequest {
    @NotBlank(message = "El título no puede estar vacío")
    @Size(min = 5, max = 100, message = "El título debe tener entre 5 y 100 caracteres")
    private String titulo;

    @NotBlank(message = "El contenido no puede estar vacío")
    @Size(min = 10, message = "El contenido debe tener al menos 10 caracteres")
    private String contenido;

    private String imagenUrl; // Opcional

    @NotNull(message = "El ID de usuario es obligatorio")
    private Long idUsuario;

    private Set<String> categorias; // Set de nombres de categorías
}