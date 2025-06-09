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
    @MapsId("idUsuario")
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonBackReference("usuario-reaccionesPublicacion")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idPublicacion")
    @JoinColumn(name = "id_publicacion", nullable = false)
    @JsonBackReference("publicacion-reacciones")
    private Publicacion publicacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_reaccion", nullable = false)
    private TipoReaccion tipoReaccion;

    @Column(name = "fecha_reaccion", nullable = false)
    private Timestamp fechaReaccion;

    public ReaccionPublicacion(Usuario usuario, Publicacion publicacion, TipoReaccion tipoReaccion) {
        this.id = new ReaccionPublicacionId(usuario.getIdUsuario(), publicacion.getIdPublicacion());
        this.usuario = usuario;
        this.publicacion = publicacion;
        this.tipoReaccion = tipoReaccion;
        this.fechaReaccion = new Timestamp(System.currentTimeMillis());
    }
}
