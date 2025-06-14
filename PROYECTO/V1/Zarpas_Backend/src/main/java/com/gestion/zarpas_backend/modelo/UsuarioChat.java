package com.gestion.zarpas_backend.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "usuario_chat")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(UsuarioChatId.class)
public class UsuarioChat implements Serializable {
    @Id
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Id
    @Column(name = "id_chat")
    private Long idChat;

    @Column(name = "fecha_union")
    private Timestamp fechaUnion;

    @ManyToOne
    @JoinColumn(name = "id_usuario", insertable = false, updatable = false)
    @JsonBackReference("usuario-usuarioChats")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_chat", insertable = false, updatable = false)
    @JsonBackReference("chat-usuarioChats")
    private Chat chat;
}