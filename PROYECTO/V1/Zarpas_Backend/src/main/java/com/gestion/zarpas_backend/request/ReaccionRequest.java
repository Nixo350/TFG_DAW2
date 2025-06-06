// src/main/java/com/gestion/zarpas_backend/request/ReaccionRequest.java
// Asegúrate de que el paquete sea el correcto para tu proyecto
package com.gestion.zarpas_backend.request;

import com.gestion.zarpas_backend.modelo.TipoReaccion; // Importa tu enum TipoReaccion
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReaccionRequest {
    private Long idUsuario;
    private Long idPublicacion;
    private TipoReaccion tipoReaccion;
}