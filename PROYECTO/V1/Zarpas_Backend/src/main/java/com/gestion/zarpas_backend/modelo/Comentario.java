package com.gestion.zarpas_backend.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comentario")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comentario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comentario")
    private Long idComentario;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonBackReference("usuario-comentarios") // Coincide con Usuario.java
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_publicacion", nullable = false)
    @JsonBackReference("publicacion-comentarios") // Coincide con Publicacion.java
    private Publicacion publicacion;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String texto;

    @Column(name = "fecha_creacion")
    private Timestamp fechaCreacion;

    @Column(name = "fecha_modificacion")
    private Timestamp fechaModificacion;

    // Relación con la entidad de unión ComentarioReaccion
    @OneToMany(mappedBy = "comentario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("comentario-comentarioReacciones")
    private List<ComentarioReaccion> reacciones = new ArrayList<>();
}