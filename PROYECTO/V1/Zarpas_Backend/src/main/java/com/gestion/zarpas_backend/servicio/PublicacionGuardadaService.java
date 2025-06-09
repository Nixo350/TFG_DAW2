package com.gestion.zarpas_backend.servicio;

import com.gestion.zarpas_backend.modelo.PublicacionGuardada;
import java.util.List;

public interface PublicacionGuardadaService {

     List<PublicacionGuardada> getPublicacionesGuardadasByUsuario(Long idUsuario) ;

}