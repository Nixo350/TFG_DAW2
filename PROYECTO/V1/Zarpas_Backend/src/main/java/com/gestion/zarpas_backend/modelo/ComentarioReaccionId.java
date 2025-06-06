package com.gestion.zarpas_backend.modelo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ComentarioReaccionId implements Serializable {
    private Long idUsuario;
    private Long idComentario;
}