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

        if (nuevoTipoReaccion == null) {
            if (existingReactionOptional.isPresent()) {
                comentarioReaccionRepository.delete(existingReactionOptional.get());
                return null;
            } else {
                return null;
            }
        } else {
            if (existingReactionOptional.isPresent()) {
                ComentarioReaccion reaccion = existingReactionOptional.get();
                if (reaccion.getTipoReaccion().equals(nuevoTipoReaccion)) {
                    comentarioReaccionRepository.delete(reaccion);
                    return null;
                } else {
                    reaccion.setTipoReaccion(nuevoTipoReaccion);
                    reaccion.setFechaReaccion(Timestamp.from(Instant.now()));
                    return comentarioReaccionRepository.save(reaccion);
                }
            } else {
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
    }

    public Map<TipoReaccion, Long> getConteoReaccionesByComentarioId(Long idComentario) {
        long likes = comentarioReaccionRepository.countByComentario_IdComentarioAndTipoReaccion(idComentario, TipoReaccion.like);
        long dislikes = comentarioReaccionRepository.countByComentario_IdComentarioAndTipoReaccion(idComentario, TipoReaccion.dislike);

        Map<TipoReaccion, Long> conteo = new HashMap<>();
        conteo.put(TipoReaccion.like, likes);
        conteo.put(TipoReaccion.dislike, dislikes);
        return conteo;
    }

    public Optional<TipoReaccion> getReaccionUsuarioAComentario(Long idUsuario, Long idComentario) {
        return comentarioReaccionRepository.findById_IdUsuarioAndId_IdComentario(idUsuario, idComentario)
                .map(ComentarioReaccion::getTipoReaccion);
    }




    @Override
    @Transactional(readOnly = true)
    public Optional<ComentarioReaccion> obtenerComentarioReaccionPorId(ComentarioReaccionId id) {
        return comentarioReaccionRepository.findById(id);
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
                return null;
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


    @Transactional(readOnly = true)
    public Optional<ComentarioReaccion> findByComentarioIdAndUsuarioId(Long idComentario, Long idUsuario) {
        return comentarioReaccionRepository.findByComentario_IdComentarioAndUsuario_IdUsuario(idComentario, idUsuario);
    }
}