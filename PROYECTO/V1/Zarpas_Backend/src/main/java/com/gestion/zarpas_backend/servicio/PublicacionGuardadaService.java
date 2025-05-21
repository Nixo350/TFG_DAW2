package com.gestion.zarpas_backend.servicio;

import com.gestion.zarpas_backend.modelo.PublicacionGuardada;
import com.gestion.zarpas_backend.modelo.PublicacionGuardadaId;
import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.modelo.Publicacion;
import java.util.List;
import java.util.Optional;

public interface PublicacionGuardadaService {
    PublicacionGuardada guardarPublicacionGuardada(PublicacionGuardada publicacionGuardada);
    Optional<PublicacionGuardada> obtenerPublicacionGuardadaPorId(PublicacionGuardadaId id);
    List<PublicacionGuardada> obtenerTodasLasPublicacionesGuardadas();
    void eliminarPublicacionGuardada(PublicacionGuardadaId id);
    List<PublicacionGuardada> obtenerPublicacionesGuardadasPorUsuario(Long idUsuario);
    PublicacionGuardada guardarPublicacionParaUsuario(Long idUsuario, Long idPublicacion) throws Exception;
    void eliminarPublicacionGuardadaParaUsuario(Long idUsuario, Long idPublicacion);
}