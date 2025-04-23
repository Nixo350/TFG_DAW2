package com.gestion.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;

import java.sql.Timestamp;

@Entity
@Table(name= "comentario_reaccion")
@Builder
public class comentario_reaccion {

    @Id
    @Column(name = "id_comentario")
    private Long id_comentario;
    @Id
    @Column(name = "id_usuario")
    private Long id_usuario;

    @Column(name = "tipo_reaccion")
    private Enum tipo_reaccion;

    @Column(name = "fecha_reaccion")
    private Timestamp fecha_reaccion;

    public comentario_reaccion() {

    }
}
