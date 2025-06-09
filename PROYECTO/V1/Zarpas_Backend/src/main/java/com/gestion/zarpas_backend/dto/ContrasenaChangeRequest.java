package com.gestion.zarpas_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContrasenaChangeRequest {
    @NotBlank(message = "La nueva contraseña no puede estar vacía.")
    @Size(min = 6, max = 40, message = "La contraseña debe tener entre 6 y 40 caracteres.")
    private String newContrasena;
}
