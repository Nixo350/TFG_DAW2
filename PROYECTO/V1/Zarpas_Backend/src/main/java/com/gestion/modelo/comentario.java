package com.gestion.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;

import java.sql.Timestamp;

@Entity
@Table(name= "comentario")
@Builder
public class comentario {


    @Id
    @Column(name = "id_comentario")
    private Long id_comentario;
    @Id
    @Column(name = "id_usuario")
    private Long id_usuario;
    @Id
    @Column(name = "id_publicacion")
    private Long id_publicacion;
    @Column(name = "texto")
    private String texto;

    @Column(name = "fecha_creacion")
    private Timestamp fecha_creacion;
    @Column(name = "fecha_modificacion")
    private Timestamp fecha_modificacion;

    public comentario() {

    }
}
