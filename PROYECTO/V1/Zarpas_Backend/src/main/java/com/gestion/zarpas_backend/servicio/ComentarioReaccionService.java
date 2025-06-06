package com.gestion.zarpas_backend.servicio;

import com.gestion.zarpas_backend.modelo.ComentarioReaccion;
import com.gestion.zarpas_backend.modelo.ComentarioReaccionId;
import com.gestion.zarpas_backend.modelo.TipoReaccion;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ComentarioReaccionService {
    ComentarioReaccion guardarComentarioReaccion(ComentarioReaccion comentarioReaccion);
    Optional<ComentarioReaccion> obtenerComentarioReaccionPorId(ComentarioReaccionId id);
    List<ComentarioReaccion> obtenerTodasLasComentarioReacciones();
    void eliminarComentarioReaccion(ComentarioReaccionId id);
    ComentarioReaccion reaccionarAComentario(Long idUsuario, Long idComentario, TipoReaccion tipoReaccion) throws Exception;
    void eliminarReaccionComentario(Long idUsuario, Long idComentario);
    List<ComentarioReaccion> obtenerReaccionesPorComentario(Long idComentario);
    List<ComentarioReaccion> obtenerReaccionesPorUsuario(Long idUsuario);
    ComentarioReaccion crearOActualizarReaccion(Long idUsuario, Long idComentario, TipoReaccion nuevoTipoReaccion);
    Optional<TipoReaccion> getReaccionUsuarioAComentario(Long idUsuario, Long idComentario);
    Map<TipoReaccion, Long> getConteoReaccionesByComentarioId(Long idComentario);
}