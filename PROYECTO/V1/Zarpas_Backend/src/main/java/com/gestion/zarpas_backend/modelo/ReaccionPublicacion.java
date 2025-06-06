package com.gestion.zarpas_backend.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "reaccion_publicacion")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReaccionPublicacion {

    @EmbeddedId
    private ReaccionPublicacionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idUsuario") // Mapea el campo idUsuario de ReaccionPublicacionId
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonBackReference("usuario-reaccionesPublicacion")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idPublicacion") // Mapea el campo idPublicacion de ReaccionPublicacionId
    @JoinColumn(name = "id_publicacion", nullable = false)
    @JsonBackReference("publicacion-reacciones")
    private Publicacion publicacion;

    @Enumerated(EnumType.STRING) // Guarda el nombre del enum (LIKE, DISLIKE)
    @Column(name = "tipo_reaccion", nullable = false)
    private TipoReaccion tipoReaccion;

    @Column(name = "fecha_reaccion", nullable = false)
    private Timestamp fechaReaccion;

    // Constructor personalizado para facilitar la creación de nuevas reacciones
    public ReaccionPublicacion(Usuario usuario, Publicacion publicacion, TipoReaccion tipoReaccion) {
        this.id = new ReaccionPublicacionId(usuario.getIdUsuario(), publicacion.getIdPublicacion());
        this.usuario = usuario;
        this.publicacion = publicacion;
        this.tipoReaccion = tipoReaccion;
        this.fechaReaccion = new Timestamp(System.currentTimeMillis());
    }
}
