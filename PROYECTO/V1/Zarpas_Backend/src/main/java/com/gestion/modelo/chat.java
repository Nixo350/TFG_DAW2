package com.gestion.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;

import java.sql.Timestamp;

@Entity
@Table(name= "chat")
@Builder
public class chat {
    @Id
    @Column(name = "id_chat")
    private Long id_chat;

    @Column(name = "nombre")
    private Timestamp nombre;

    @Column(name = "fecha_creacion")
    private Timestamp fecha_creacion;

    @Column(name = "fecha_modificacion")
    private Timestamp fecha_modificacion;


    public chat() {

    }

}
