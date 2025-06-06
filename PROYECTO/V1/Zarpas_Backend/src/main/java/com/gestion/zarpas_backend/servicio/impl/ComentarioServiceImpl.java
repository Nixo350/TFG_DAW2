package com.gestion.zarpas_backend.servicio.impl;

import com.gestion.zarpas_backend.modelo.Comentario;
import com.gestion.zarpas_backend.modelo.Publicacion;
import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.repositorio.ComentarioRepository;
import com.gestion.zarpas_backend.repositorio.PublicacionRepository;
import com.gestion.zarpas_backend.repositorio.UsuarioRepository;
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
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PublicacionRepository publicacionRepository;

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
    @Transactional
    public Comentario crearComentario(Long idUsuario, Long idPublicacion, String texto) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));
        Publicacion publicacion = publicacionRepository.findById(idPublicacion)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada con ID: " + idPublicacion));

        Comentario comentario = new Comentario();
        comentario.setUsuario(usuario);
        comentario.setPublicacion(publicacion);
        comentario.setTexto(texto);
        comentario.setFechaCreacion(new Timestamp(System.currentTimeMillis()));
        comentario.setFechaModificacion(new Timestamp(System.currentTimeMillis()));
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
    public List<Comentario> obtenerComentariosPorPublicacion(Long idPublicacion) {
        return comentarioRepository.findByPublicacion_IdPublicacionOrderByFechaCreacionAsc(idPublicacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comentario> obtenerComentariosPorUsuario(Usuario usuario) {
        return comentarioRepository.findByUsuario(usuario);
    }

    @Override
    @Transactional
    public Comentario actualizarComentario(Long idComentario, String nuevoTexto) {
        Comentario comentario = comentarioRepository.findById(idComentario)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado con ID: " + idComentario));
        comentario.setTexto(nuevoTexto);
        comentario.setFechaModificacion(new Timestamp(System.currentTimeMillis()));
        return comentarioRepository.save(comentario);
    }

    @Override
    @Transactional
    public void eliminarComentario(Long id) {
        comentarioRepository.deleteById(id);
    }
}