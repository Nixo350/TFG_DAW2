package com.gestion.zarpas_backend.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "mensaje")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mensaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mensaje")
    private Long idMensaje;

    @ManyToOne
    @JoinColumn(name = "id_chat", nullable = false)
    @JsonBackReference("chat-mensajes") // Coincide con Chat.java
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "id_emisor", nullable = false)
    @JsonBackReference("usuario-mensajesEnviados") // Coincide con Usuario.java
    private Usuario emisor;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "fecha_envio")
    private Timestamp fechaEnvio;

    private Boolean leido;
}