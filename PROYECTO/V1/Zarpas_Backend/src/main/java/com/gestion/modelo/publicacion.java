package com.gestion.modelo;

import jakarta.persistence.*;
import lombok.Builder;

import java.sql.Timestamp;

@Entity
@Table(name= "publicacion")
@Builder
public class publicacion {

    @Id
    @Column(name = "id_publicacion")
    private Long id_publicacion;
    @Id
    @Column(name = "id_usuario")
    private Long id_usuario;
    @Column(name = "titulo")
    private String titulo;
    @Column(name = "contenido")
    private String contenido;
    @Column(name = "fecha_creacion")
    private Timestamp fecha_creacion;
    @Column(name = "fecha_modificacion")
    private Timestamp fecha_modificacion;
    public publicacion() {

    }
}
