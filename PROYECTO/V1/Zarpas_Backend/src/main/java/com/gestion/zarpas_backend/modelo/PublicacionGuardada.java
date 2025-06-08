package com.gestion.zarpas_backend.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "publicacion_guardada")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PublicacionGuardadaId.class)
public class PublicacionGuardada implements Serializable {


    @Id
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Id
    @Column(name = "id_publicacion")
    private Long idPublicacion;

    @Column(name = "fecha_guardado")
    private Timestamp fechaGuardado;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idUsuario")
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    @JsonBackReference("usuario-publicacionGuardadas")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idPublicacion")
    @JoinColumn(name = "id_publicacion", referencedColumnName = "id_publicacion")
    @JsonBackReference("publicacion-publicacionGuardadas")
    private Publicacion publicacion;


}