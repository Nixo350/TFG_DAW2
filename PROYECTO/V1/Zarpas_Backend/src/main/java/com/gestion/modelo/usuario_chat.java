package com.gestion.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;

import java.sql.Timestamp;

@Entity
@Table(name= "usuario_chat")
@Builder
public class usuario_chat {

    @Id
    @Column(name = "id_chat")
    private Long id_chat;
    @Id
    @Column(name = "id_usuario")
    private Long id_usuario;

    @Column(name = "fecha_reunion")
    private Timestamp fecha_reunion;

    public usuario_chat() {

    }
}
