package com.gestion.zarpas_backend.modelo;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable; // Necesario para @Embeddable y @IdClass
import java.util.Objects;
import java.sql.Timestamp;

// Clase para la clave primaria compuesta
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ReaccionPublicacionId implements Serializable {

    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "id_publicacion")
    private Long idPublicacion;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReaccionPublicacionId that = (ReaccionPublicacionId) o;
        return Objects.equals(idUsuario, that.idUsuario) &&
                Objects.equals(idPublicacion, that.idPublicacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, idPublicacion);
    }
}

