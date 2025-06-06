package com.gestion.zarpas_backend.modelo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PublicacionGuardadaId implements Serializable {
    private Long idUsuario;
    private Long idPublicacion;
}