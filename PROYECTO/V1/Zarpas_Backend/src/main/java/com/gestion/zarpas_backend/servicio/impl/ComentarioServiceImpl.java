package com.gestion.zarpas_backend.servicio.impl;

import com.gestion.zarpas_backend.modelo.Comentario;
import com.gestion.zarpas_backend.modelo.Publicacion;
import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.repositorio.ComentarioRepository;
import com.gestion.zarpas_backend.servicio.ComentarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ComentarioServiceImpl implements ComentarioService {

    private final ComentarioRepository comentarioRepository;

    @Autowired
    public ComentarioServiceImpl(ComentarioRepository comentarioRepository) {
        this.comentarioRepository = comentarioRepository;
    }

    @Override
    @Transactional
    public Comentario guardarComentario(Comentario comentario) {
        if (comentario.getFechaCreacion() == null) {
            comentario.setFechaCreacion(Timestamp.from(Instant.now()));
        }
        comentario.setFechaModificacion(Timestamp.from(Instant.now()));
        return comentarioRepository.save(comentario);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Comentario> obtenerComentarioPorId(Long id) {
        return comentarioRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comentario> obtenerTodosLosComentarios() {
        return comentarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comentario> obtenerComentariosPorPublicacion(Publicacion publicacion) {
        return comentarioRepository.findByPublicacion(publicacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comentario> obtenerComentariosPorUsuario(Usuario usuario) {
        return comentarioRepository.findByUsuario(usuario);
    }

    @Override
    @Transactional
    public Comentario actualizarComentario(Comentario comentario) {
        return comentarioRepository.findById(comentario.getIdComentario())
                .map(existingComentario -> {
                    existingComentario.setTexto(comentario.getTexto());
                    existingComentario.setFechaModificacion(Timestamp.from(Instant.now()));
                    return comentarioRepository.save(existingComentario);
                }).orElseThrow(() -> new RuntimeException("Comentario no encontrado con ID: " + comentario.getIdComentario()));
    }

    @Override
    @Transactional
    public void eliminarComentario(Long id) {
        comentarioRepository.deleteById(id);
    }
}