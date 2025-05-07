package com.gestion.zarpas_backend.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private String email;

    private String nombre;

    @Column(nullable = false)
    private String contrasena;

    @Column(name = "fecha_registro")
    private Timestamp fechaRegistro;

    @Column(name = "ultimo_login")
    private Timestamp ultimoLogin;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Publicacion> publicaciones;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Comentario> comentarios;

    @ManyToMany
    @JoinTable(
            name = "USUARIO_CHAT",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_chat")
    )
    @JsonManagedReference
    private List<Chat> chats;

    @OneToMany(mappedBy = "emisor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Mensaje> mensajesEnviados;

    @ManyToMany
    @JoinTable(
            name = "PUBLICACION_GUARDADA",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_publicacion")
    )
    @JsonManagedReference
    private List<Publicacion> publicacionesGuardadas;

    @ManyToMany
    @JoinTable(
            name = "COMENTARIO_REACCION",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_comentario")
    )
    @JsonManagedReference
    private List<Comentario> comentariosReaccionados;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER,mappedBy = "usuario")
    @JsonBackReference
    private Set<UsuarioRol> usuarioRoles = new HashSet<>();
}