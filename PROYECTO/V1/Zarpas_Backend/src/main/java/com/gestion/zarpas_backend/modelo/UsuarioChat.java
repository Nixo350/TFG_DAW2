package com.gestion.zarpas_backend.modelo;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "USUARIO_CHAT")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(UsuarioChatId.class)
public class UsuarioChat {
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
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_chat", insertable = false, updatable = false)
    private Chat chat;
}

@Data
class UsuarioChatId implements Serializable {
    private Long idUsuario;
    private Long idChat;
}