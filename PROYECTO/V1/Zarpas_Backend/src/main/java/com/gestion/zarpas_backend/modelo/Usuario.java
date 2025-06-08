package com.gestion.zarpas_backend.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private String nombre;

    @Column(nullable = false)
    private String contrasena;

    @Column(name = "fecha_registro")
    private Timestamp fechaRegistro;

    @Column(name = "ultimo_login")
    private Timestamp ultimoLogin;

    @Column(name = "foto_perfil")
    private String fotoPerfil;

    // Relación con Publicacion (publicaciones creadas por el usuario)
    // El usuario es el lado "managed" (posee la lista de publicaciones)
    // La Publicacion tendrá @JsonBackReference("usuario-publicaciones")
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("usuario-publicaciones") // AÑADE ESTO
    // @JsonIgnore // <-- Elimina este JsonIgnore si quieres serializar las publicaciones del usuario en algunos contextos
    private List<Publicacion> publicaciones;

    // Relación con Comentario
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("usuario-comentarios") // El usuario "gestiona" sus comentarios
    // @JsonIgnore // <-- Elimina este JsonIgnore si quieres serializar los comentarios del usuario
    private List<Comentario> comentarios = new ArrayList<>();

    // Relación con Chat (el usuario participa en muchos chats)
    // Usa @JsonIgnore aquí si no necesitas serializar los chats del usuario al obtener un Usuario
    // O bien, usa @JsonManagedReference si el Chat tiene un @JsonBackReference al usuario.
    @ManyToMany
    @JoinTable(
            name = "usuario_chat",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_chat")
    )
    @JsonIgnore // Considera usar @JsonIgnore aquí si serializar los chats del usuario causa problemas recursivos grandes
    private List<Chat> chats;

    // Relación con Mensaje (mensajes enviados por el usuario)
    @OneToMany(mappedBy = "emisor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("usuario-mensajesEnviados") // El usuario "gestiona" sus mensajes enviados
    private List<Mensaje> mensajesEnviados;

    // --- CORRECCIÓN IMPORTANTE PARA PUBLICACIONES GUARDADAS ---
    // Si PublicacionGuardada es una entidad de unión, la relación aquí debe ser OneToMany
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("usuario-publicacionGuardadas") // El usuario "gestiona" sus publicaciones guardadas
    // @JsonIgnore // <-- Puedes eliminar esto si necesitas serializar las PublicacionGuardada completas
    private List<PublicacionGuardada> publicacionesGuardadas;


    // Relación con ComentarioReaccion
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    @JsonManagedReference("usuario-comentarioReacciones") // El usuario "gestiona" sus reacciones a comentarios
    private List<ComentarioReaccion> comentarioReacciones = new ArrayList<>();

    // Relación de Usuario-Rol
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER,mappedBy = "usuario")
    @JsonManagedReference("usuario-usuarioRoles") // El usuario "gestiona" sus roles
    // @JsonIgnore // <-- Puedes eliminar esto si necesitas serializar los roles del usuario
    private Set<UsuarioRol> usuarioRoles = new HashSet<>();

    // Relación con ReaccionPublicacion (reacciones del usuario a publicaciones)
    // Asumo que 'ReaccionPublicacion' tiene un campo 'usuario' que mapea aquí
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("usuario-reaccionesPublicacion") // El usuario "gestiona" sus reacciones a publicaciones
    private List<ReaccionPublicacion> reaccionesPublicacion = new ArrayList<>();


    // Constructor para el registro de usuario
    public Usuario(String username, String email, String contrasena, String nombre) {
        this.username = username;
        this.email = email;
        this.contrasena = contrasena;
        this.nombre = nombre;
        this.fechaRegistro = new Timestamp(System.currentTimeMillis()); // Corregido el typo
        this.usuarioRoles = new HashSet<>();
        this.comentarios = new ArrayList<>();
        this.reaccionesPublicacion = new ArrayList<>();
        this.comentarioReacciones = new ArrayList<>();
        this.publicaciones = new ArrayList<>(); // Inicializa también
        this.chats = new ArrayList<>(); // Inicializa también
        this.mensajesEnviados = new ArrayList<>(); // Inicializa también
        this.publicacionesGuardadas = new ArrayList<>(); // Inicializa también
    }

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