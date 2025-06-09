package com.gestion.zarpas_backend.dto;

import com.gestion.zarpas_backend.modelo.TipoReaccion;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReaccionPublicacionRequest {
    private Long idUsuario;
    private Long idPublicacion;
    private TipoReaccion tipoReaccion;
}