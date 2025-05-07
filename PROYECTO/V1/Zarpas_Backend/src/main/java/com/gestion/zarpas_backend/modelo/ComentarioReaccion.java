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
@AllArgsConstructor
@IdClass(ComentarioReaccionId.class)
public class ComentarioReaccion {
    @Id
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Id
    @Column(name = "id_comentario")
    private Long idComentario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_reaccion")
    private TipoReaccion tipoReaccion;

    @Column(name = "fecha_reaccion")
    private Timestamp fechaReaccion;

    @ManyToOne
    @JoinColumn(name = "id_usuario", insertable = false, updatable = false)
    @JsonBackReference
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_comentario", insertable = false, updatable = false)
    @JsonBackReference
    private Comentario comentario;
}

@Data
class ComentarioReaccionId implements Serializable {
    private Long idUsuario;
    private Long idComentario;
}