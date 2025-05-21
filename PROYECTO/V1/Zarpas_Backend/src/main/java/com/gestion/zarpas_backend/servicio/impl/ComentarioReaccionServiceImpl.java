package com.gestion.zarpas_backend.servicio.impl;

import com.gestion.zarpas_backend.modelo.ComentarioReaccion;
import com.gestion.zarpas_backend.modelo.ComentarioReaccionId;
import com.gestion.zarpas_backend.modelo.TipoReaccion;
import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.repositorio.ComentarioReaccionRepository;
import com.gestion.zarpas_backend.repositorio.UsuarioRepository;
import com.gestion.zarpas_backend.repositorio.ComentarioRepository;
import com.gestion.zarpas_backend.servicio.ComentarioReaccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
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
            cr.setIdUsuario(idUsuario);
            cr.setIdComentario(idComentario);
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
        return comentarioReaccionRepository.findByIdComentario(idComentario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComentarioReaccion> obtenerReaccionesPorUsuario(Long idUsuario) {
        return comentarioReaccionRepository.findByIdUsuario(idUsuario);
    }
}