package com.gestion.zarpas_backend.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.*;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@Builder
@NoArgsConstructor // Necesario para JPA y JSON
@AllArgsConstructor // Necesario para Builder y algunos usos de conversión
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    // AÑADE ESTE CAMPO PARA EL NOMBRE DE USUARIO
    @Column(unique = true, nullable = false) // Asegúrate de que sea único
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private String nombre;

    @Column(nullable = false)
    private String contrasena; // Cambiado a 'contrasena' para que coincida con el frontend

    @Column(name = "fecha_registro")
    private Timestamp fechaRegistro;

    @Column(name = "ultimo_login")
    private Timestamp ultimoLogin;

    @Column(name = "foto_perfil")
    private String fotoPerfil;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("usuario-publicaciones")
    @JsonIgnore// Nombre único
    private List<Publicacion> publicaciones; //

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("usuario-comentarios")
    @JsonIgnore// Nombre único
    private List<Comentario> comentarios = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "usuario_chat",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_chat")
    )
    @JsonManagedReference("usuario-chats") // Nombre único
    private List<Chat> chats; //

    @OneToMany(mappedBy = "emisor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("usuario-mensajesEnviados") // Nombre único
    private List<Mensaje> mensajesEnviados; //

    @ManyToMany
    @JoinTable(
            name = "publicacion_guardada",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_publicacion")
    )
    @JsonManagedReference("usuario-publicacionesGuardadas") // Nombre único
    private List<Publicacion> publicacionesGuardadas; //


    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("usuario-comentarioReacciones")
    private List<ComentarioReaccion> comentarioReacciones = new ArrayList<>();

    // Relación de Usuario-Rol (a través de UsuarioRol)
    // Usamos JsonManagedReference para gestionar esta parte de la relación
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER,mappedBy = "usuario")
    @JsonManagedReference("usuario-usuarioRoles") // Nombre único
    @JsonIgnore
    private Set<UsuarioRol> usuarioRoles = new HashSet<>(); //

    // --- ¡AÑADE ESTO PARA LAS REACCIONES A PUBLICACIONES! ---
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("usuario-reaccionesPublicacion")
    private List<ReaccionPublicacion> reaccionesPublicacion = new ArrayList<>();


    // Constructor para el registro de usuario (simplificado, puedes ajustarlo si necesitas más campos)
    public Usuario(String username, String email, String contrasena, String nombre) {
        this.username = username;
        this.email = email;
        this.contrasena = contrasena;
        this.nombre = nombre;
        this.fechaRegistro = new Timestamp(System.currentTimeMillis());
        this.usuarioRoles = new HashSet<>(); // Inicializar roles vacíos, se asignarán después
    }

    // Métodos equals y hashCode para UserDetailsImpl
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(idUsuario, usuario.idUsuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario);
    }
}