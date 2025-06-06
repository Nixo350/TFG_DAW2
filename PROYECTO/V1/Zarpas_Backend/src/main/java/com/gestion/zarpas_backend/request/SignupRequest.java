package com.gestion.zarpas_backend.request; // Asegúrate de que este sea el paquete correcto

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Esta clase representa los datos que el frontend enviará al backend para el registro
public class SignupRequest {

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 40) // La contraseña debería tener una longitud razonable
    private String contrasena; // Coincide con el nombre de campo en tu entidad Usuario y frontend

    @Size(max = 100) // Campo opcional
    private String nombre; // Coincide con el nombre de campo en tu entidad Usuario y frontend

    // Getters y Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}