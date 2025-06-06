package com.gestion.zarpas_backend.modelo;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;


@Data
@Embeddable
public class ComentarioReaccionId implements Serializable {
    private Long idUsuario;
    private Long idComentario;
    public ComentarioReaccionId() {}

    public ComentarioReaccionId(Long idUsuario, Long idComentario) {
        this.idUsuario = idUsuario;
        this.idComentario = idComentario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComentarioReaccionId that = (ComentarioReaccionId) o;
        return Objects.equals(idUsuario, that.idUsuario) &&
                Objects.equals(idComentario, that.idComentario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, idComentario);
    }
}