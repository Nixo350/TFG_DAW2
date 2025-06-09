package com.gestion.zarpas_backend.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "comentario_reaccion")
@Getter
@Setter
@Builder
@NoArgsConstructor
public class ComentarioReaccion implements Serializable {

    @EmbeddedId
    private ComentarioReaccionId id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_reaccion")
    private TipoReaccion tipoReaccion;

    @Column(name = "fecha_reaccion")
    private Timestamp fechaReaccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", insertable = false, updatable = false)
    @JsonBackReference("usuario-comentarioReacciones")
    @MapsId("idUsuario")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comentario", insertable = false, updatable = false)
    @MapsId("idComentario")
    @JsonBackReference("comentario-comentarioReacciones")
    private Comentario comentario;

    public ComentarioReaccion(Usuario usuario, Comentario comentario, TipoReaccion tipoReaccion) {
        this.id = new ComentarioReaccionId(usuario.getIdUsuario(), comentario.getIdComentario());
        this.usuario = usuario;
        this.comentario = comentario;
        this.tipoReaccion = tipoReaccion;
        this.fechaReaccion = new Timestamp(System.currentTimeMillis());
    }
    public ComentarioReaccion(ComentarioReaccionId id, TipoReaccion tipoReaccion, Timestamp fechaReaccion, Usuario usuario, Comentario comentario) {
        this.id = id;
        this.tipoReaccion = tipoReaccion;
        this.fechaReaccion = fechaReaccion;
        this.usuario = usuario;
        this.comentario = comentario;
    }

    public Long getIdUsuario() {
        return this.id != null ? this.id.getIdUsuario() : null;
    }

    public Long getIdComentario() {
        return this.id != null ? this.id.getIdComentario() : null;
    }
}