package com.gestion.zarpas_backend.modelo;

import com.fasterxml.jackson.annotation.JsonManagedReference; // Asegúrate de usar esta importación
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario_rol")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long UsuarioRolId; // Un ID propio para la tabla de unión

    @ManyToOne(fetch = FetchType.EAGER) // Fetch EAGER para cargar roles con el usuario
    @JoinColumn(name = "id_usuario")
    @JsonManagedReference("usuario-usuarioRoles") // Coincide con el nombre en Usuario.java
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_rol")
    @JsonManagedReference("rol-usuarioRoles") // Coincide con el nombre en Rol.java
    private Rol rol;

    // Si necesitas un constructor para crear fácilmente UsuarioRol
    public UsuarioRol(Usuario usuario, Rol rol) {
        this.usuario = usuario;
        this.rol = rol;
    }
}