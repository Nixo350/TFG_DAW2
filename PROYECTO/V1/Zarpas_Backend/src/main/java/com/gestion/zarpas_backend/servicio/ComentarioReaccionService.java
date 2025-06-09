package com.gestion.zarpas_backend.servicio;

import com.gestion.zarpas_backend.modelo.ComentarioReaccion;
import com.gestion.zarpas_backend.modelo.ComentarioReaccionId;
import com.gestion.zarpas_backend.modelo.TipoReaccion;

import java.util.Map;
import java.util.Optional;

public interface ComentarioReaccionService {



    Optional<ComentarioReaccion> obtenerComentarioReaccionPorId(ComentarioReaccionId id);

    ComentarioReaccion reaccionarAComentario(Long idUsuario, Long idComentario, TipoReaccion tipoReaccion) throws Exception;

    ComentarioReaccion crearOActualizarReaccion(Long idUsuario, Long idComentario, TipoReaccion nuevoTipoReaccion);

    Map<TipoReaccion, Long> getConteoReaccionesByComentarioId(Long idComentario);

     Optional<ComentarioReaccion> findByComentarioIdAndUsuarioId(Long idComentario, Long idUsuario) ;
}