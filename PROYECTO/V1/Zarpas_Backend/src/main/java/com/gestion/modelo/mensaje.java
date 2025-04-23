package com.gestion.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;

import java.sql.Timestamp;

@Entity
@Table(name= "mensaje")
@Builder
public class mensaje {
    @Id
    @Column(name = "id_mensaje")
    private Long id_mensaje;
    @Id
    @Column(name = "id_chat")
    private Long id_chat;
    @Id
    @Column(name = "id_emisor")
    private Long id_emisor;
    @Column(name = "contenido")
    private String contenido;

    @Column(name = "fecha_envio")
    private Timestamp fecha_envio;
    @Column(name = "leido")
    private int leido;

    public mensaje() {

    }
}
