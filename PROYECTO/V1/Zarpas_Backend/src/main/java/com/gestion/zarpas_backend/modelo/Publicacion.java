package com.gestion.zarpas_backend.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet; // Aunque no se usa directamente en Publicacion, se mantiene por si acaso
import java.util.List;
import java.util.Set; // Aunque no se usa directamente en Publicacion, se mantiene por si acaso

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonBackReference("usuario-publicaciones") // <--- ¡CORREGIDO! Ahora es JsonBackReference
    private Usuario usuario; // El usuario que creó la publicación

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
    @JsonManagedReference("publicacion-comentarios") // Correcto: la publicación gestiona sus comentarios
    private List<Comentario> comentarios = new ArrayList<>();

    // Relación con la entidad de unión PublicacionGuardada
    @OneToMany(mappedBy = "publicacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("publicacion-publicacionGuardadas") // <--- ¡CORREGIDO! Ahora es JsonManagedReference
    private List<PublicacionGuardada> guardadosPorUsuarios = new ArrayList<>();

    // Relación con ReaccionPublicacion
    @OneToMany(mappedBy = "publicacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("publicacion-reacciones") // Correcto: la publicación gestiona sus reacciones
    private List<ReaccionPublicacion> reaccionesPublicacion = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;
}