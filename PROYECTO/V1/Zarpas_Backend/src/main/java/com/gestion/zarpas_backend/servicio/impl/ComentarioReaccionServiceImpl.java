package com.gestion.zarpas_backend.servicio.impl;

import com.gestion.zarpas_backend.modelo.*;
import com.gestion.zarpas_backend.repositorio.ComentarioReaccionRepository;
import com.gestion.zarpas_backend.repositorio.UsuarioRepository;
import com.gestion.zarpas_backend.repositorio.ComentarioRepository;
import com.gestion.zarpas_backend.servicio.ComentarioReaccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ComentarioReaccionServiceImpl implements ComentarioReaccionService {

    private final ComentarioReaccionRepository comentarioReaccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final ComentarioRepository comentarioRepository;

    @Autowired
    public ComentarioReaccionServiceImpl(ComentarioReaccionRepository comentarioReaccionRepository,
                                         UsuarioRepository usuarioRepository,
                                         ComentarioRepository comentarioRepository) {
        this.comentarioReaccionRepository = comentarioReaccionRepository;
        this.usuarioRepository = usuarioRepository;
        this.comentarioRepository = comentarioRepository;
    }
    @Transactional
    @Override
    public ComentarioReaccion crearOActualizarReaccion(Long idUsuario, Long idComentario, TipoReaccion nuevoTipoReaccion) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + idUsuario));
        Comentario comentario = comentarioRepository.findById(idComentario)
                .orElseThrow(() -> new IllegalArgumentException("Comentario no encontrado con ID: " + idComentario));

        Optional<ComentarioReaccion> existingReactionOptional = comentarioReaccionRepository
                .findById_IdUsuarioAndId_IdComentario(idUsuario, idComentario);

        // --- INICIO DE LA LÓGICA CORREGIDA ---
        if (nuevoTipoReaccion == null) {
            // Si nuevoTipoReaccion es null, el usuario quiere deseleccionar/eliminar su reacción.
            if (existingReactionOptional.isPresent()) {
                comentarioReaccionRepository.delete(existingReactionOptional.get());
                return null; // Indica que la reacción fue eliminada
            } else {
                return null; // No hay reacción existente que eliminar
            }
        } else {
            // Si nuevoTipoReaccion no es null, el usuario quiere reaccionar o cambiar su reacción.
            if (existingReactionOptional.isPresent()) {
                ComentarioReaccion reaccion = existingReactionOptional.get();
                if (reaccion.getTipoReaccion().equals(nuevoTipoReaccion)) {
                    // Si el tipo de reacción existente es el mismo que el nuevo, se elimina (desactivar).
                    comentarioReaccionRepository.delete(reaccion);
                    return null; // Indica que la reacción fue eliminada
                } else {
                    // Si el tipo de reacción existente es diferente, se actualiza.
                    reaccion.setTipoReaccion(nuevoTipoReaccion);
                    reaccion.setFechaReaccion(Timestamp.from(Instant.now()));
                    return comentarioReaccionRepository.save(reaccion);
                }
            } else {
                // No hay reacción existente, se crea una nueva.
                ComentarioReaccion nuevaReaccion = new ComentarioReaccion();
                if (nuevaReaccion.getId() == null) {
                    nuevaReaccion.setId(new ComentarioReaccionId());
                }
                nuevaReaccion.getId().setIdUsuario(idUsuario);
                nuevaReaccion.getId().setIdComentario(idComentario);
                nuevaReaccion.setUsuario(usuario);
                nuevaReaccion.setComentario(comentario);
                nuevaReaccion.setTipoReaccion(nuevoTipoReaccion);
                nuevaReaccion.setFechaReaccion(Timestamp.from(Instant.now()));
                return comentarioReaccionRepository.save(nuevaReaccion);
            }
        }
        // --- FIN DE LA LÓGICA CORREGIDA ---
    }

    public Map<TipoReaccion, Long> getConteoReaccionesByComentarioId(Long idComentario) {
        // Mejorar para que devuelva siempre 0 si no hay reacciones en lugar de no incluir la clave
        long likes = comentarioReaccionRepository.countByComentario_IdComentarioAndTipoReaccion(idComentario, TipoReaccion.like);
        long dislikes = comentarioReaccionRepository.countByComentario_IdComentarioAndTipoReaccion(idComentario, TipoReaccion.dislike);

        Map<TipoReaccion, Long> conteo = new HashMap<>();
        conteo.put(TipoReaccion.like, likes);
        conteo.put(TipoReaccion.dislike, dislikes);
        return conteo;
    }

    // Obtener el tipo de reacción de un usuario a un comentario (si existe)
    public Optional<TipoReaccion> getReaccionUsuarioAComentario(Long idUsuario, Long idComentario) {
        return comentarioReaccionRepository.findById_IdUsuarioAndId_IdComentario(idUsuario, idComentario)
                .map(ComentarioReaccion::getTipoReaccion);
    }


    @Override
    @Transactional
    public ComentarioReaccion guardarComentarioReaccion(ComentarioReaccion comentarioReaccion) {
        if (comentarioReaccion.getFechaReaccion() == null) {
            comentarioReaccion.setFechaReaccion(Timestamp.from(Instant.now()));
        }
        return comentarioReaccionRepository.save(comentarioReaccion);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ComentarioReaccion> obtenerComentarioReaccionPorId(ComentarioReaccionId id) {
        return comentarioReaccionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComentarioReaccion> obtenerTodasLasComentarioReacciones() {
        return comentarioReaccionRepository.findAll();
    }

    @Override
    @Transactional
    public void eliminarComentarioReaccion(ComentarioReaccionId id) {
        comentarioReaccionRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ComentarioReaccion reaccionarAComentario(Long idUsuario, Long idComentario, TipoReaccion tipoReaccion) throws Exception {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));
        com.gestion.zarpas_backend.modelo.Comentario comentario = comentarioRepository.findById(idComentario)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado con ID: " + idComentario));

        ComentarioReaccionId id = new ComentarioReaccionId();
        id.setIdUsuario(idUsuario);
        id.setIdComentario(idComentario);

        Optional<ComentarioReaccion> existingReaction = comentarioReaccionRepository.findById(id);

        ComentarioReaccion cr;
        if (existingReaction.isPresent()) {
            cr = existingReaction.get();
            if (cr.getTipoReaccion().equals(tipoReaccion)) {
                comentarioReaccionRepository.delete(cr);
                return null; // O podrías devolver un DTO indicando que la reacción fue eliminada
            } else {
                cr.setTipoReaccion(tipoReaccion);
                cr.setFechaReaccion(Timestamp.from(Instant.now()));
            }
        } else {
            cr = new ComentarioReaccion();
            if (cr.getId() == null) {
                cr.setId(new ComentarioReaccionId());
            }
            cr.getId().setIdUsuario(idUsuario);
            cr.getId().setIdComentario(idComentario);
            cr.setUsuario(usuario);
            cr.setComentario(comentario);
            cr.setTipoReaccion(tipoReaccion);
            cr.setFechaReaccion(Timestamp.from(Instant.now()));
        }
        return comentarioReaccionRepository.save(cr);
    }

    @Override
    @Transactional
    public void eliminarReaccionComentario(Long idUsuario, Long idComentario) {
        ComentarioReaccionId id = new ComentarioReaccionId();
        id.setIdUsuario(idUsuario);
        id.setIdComentario(idComentario);
        comentarioReaccionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComentarioReaccion> obtenerReaccionesPorComentario(Long idComentario) {
        return comentarioReaccionRepository.findById_IdComentario(idComentario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComentarioReaccion> obtenerReaccionesPorUsuario(Long idUsuario) {
        return comentarioReaccionRepository.findById_IdUsuario(idUsuario);
    }
    @Transactional(readOnly = true)
    public Optional<ComentarioReaccion> findByComentarioIdAndUsuarioId(Long idComentario, Long idUsuario) {
        return comentarioReaccionRepository.findByComentario_IdComentarioAndUsuario_IdUsuario(idComentario, idUsuario);
    }
}