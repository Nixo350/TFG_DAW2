package com.gestion.zarpas_backend.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "publicacion")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Publicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_publicacion")
    private Long idPublicacion;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonBackReference("usuario-publicaciones") // Coincide con Usuario.java
    private Usuario usuario;

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(name = "fecha_creacion")
    private Timestamp fechaCreacion;

    @Column(name = "fecha_modificacion")
    private Timestamp fechaModificacion;

    @OneToMany(mappedBy = "publicacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("publicacion-comentarios") // Coincide con Comentario.java
    private List<Comentario> comentarios = new ArrayList<>();

    // Relación con la entidad de unión PublicacionGuardada
    @OneToMany(mappedBy = "publicacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference("publicacion-publicacionGuardadas") // Coincide con PublicacionGuardada.java
    private List<PublicacionGuardada> guardadosPorUsuarios = new ArrayList<>();

    // --- ¡AÑADE ESTO PARA LAS REACCIONES! ---
    @OneToMany(mappedBy = "publicacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("publicacion-reacciones")
    private List<ReaccionPublicacion> reaccionesPublicacion = new ArrayList<>();

}