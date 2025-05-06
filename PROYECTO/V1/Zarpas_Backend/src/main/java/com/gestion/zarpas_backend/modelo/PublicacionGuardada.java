package com.gestion.zarpas_backend.modelo;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "PUBLICACION_GUARDADA")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PublicacionGuardadaId.class)
public class PublicacionGuardada {
    @Id
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Id
    @Column(name = "id_publicacion")
    private Long idPublicacion;

    @Column(name = "fecha_guardado")
    private Timestamp fechaGuardado;

    @ManyToOne
    @JoinColumn(name = "id_usuario", insertable = false, updatable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_publicacion", insertable = false, updatable = false)
    private Publicacion publicacion;
}

@Data
class PublicacionGuardadaId implements Serializable {
    private Long idUsuario;
    private Long idPublicacion;
}