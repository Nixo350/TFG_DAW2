package com.gestion.zarpas_backend.request;

import com.gestion.zarpas_backend.modelo.TipoReaccion;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReaccionRequest {
    private Long idUsuario;
    private Long idPublicacion;
    private TipoReaccion tipoReaccion;
}