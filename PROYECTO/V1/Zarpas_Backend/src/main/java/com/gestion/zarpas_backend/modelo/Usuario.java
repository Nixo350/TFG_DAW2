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

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("usuario-publicaciones")
    private List<Publicacion> publicaciones;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("usuario-comentarios")
    private List<Comentario> comentarios = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "usuario_chat",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_chat")
    )
    @JsonIgnore
    private List<Chat> chats;

    @OneToMany(mappedBy = "emisor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("usuario-mensajesEnviados")
    private List<Mensaje> mensajesEnviados;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("usuario-publicacionGuardadas")
    private List<PublicacionGuardada> publicacionesGuardadas;


    // Relación con ComentarioReaccion
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    @JsonManagedReference("usuario-comentarioReacciones")
    private List<ComentarioReaccion> comentarioReacciones = new ArrayList<>();

    // Relación de Usuario-Rol
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER,mappedBy = "usuario")
    @JsonManagedReference("usuario-usuarioRoles")
    private Set<UsuarioRol> usuarioRoles = new HashSet<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("usuario-reaccionesPublicacion")
    private List<ReaccionPublicacion> reaccionesPublicacion = new ArrayList<>();


    // Constructor para el registro de usuario
    public Usuario(String username, String email, String contrasena, String nombre) {
        this.username = username;
        this.email = email;
        this.contrasena = contrasena;
        this.nombre = nombre;
        this.fechaRegistro = new Timestamp(System.currentTimeMillis());
        this.usuarioRoles = new HashSet<>();
        this.comentarios = new ArrayList<>();
        this.reaccionesPublicacion = new ArrayList<>();
        this.comentarioReacciones = new ArrayList<>();
        this.publicaciones = new ArrayList<>();
        this.chats = new ArrayList<>();
        this.mensajesEnviados = new ArrayList<>();
        this.publicacionesGuardadas = new ArrayList<>();
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