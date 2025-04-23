package com.gestion.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;

import java.sql.Timestamp;

@Entity
@Table(name= "publicacion_guardada")
@Builder
public class publicacion_guardada {
    @Id
    @Column(name = "id_publicacion")
    private Long id_publicacion;
    @Id
    @Column(name = "id_usuario")
    private Long id_usuario;


    @Column(name = "fecha_guardado")
    private Timestamp fecha_guardado;

    public publicacion_guardada() {

    }
}
