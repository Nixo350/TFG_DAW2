package com.gestion.zarpas_backend.servicio.impl;

import com.gestion.zarpas_backend.modelo.PublicacionGuardada;
import com.gestion.zarpas_backend.modelo.PublicacionGuardadaId;
import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.modelo.Publicacion;
import com.gestion.zarpas_backend.repositorio.PublicacionGuardadaRepository;
import com.gestion.zarpas_backend.repositorio.UsuarioRepository;
import com.gestion.zarpas_backend.repositorio.PublicacionRepository;
import com.gestion.zarpas_backend.servicio.PublicacionGuardadaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class PublicacionGuardadaServiceImpl implements PublicacionGuardadaService {

    private final PublicacionGuardadaRepository publicacionGuardadaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PublicacionRepository publicacionRepository;

    @Autowired
    public PublicacionGuardadaServiceImpl(PublicacionGuardadaRepository publicacionGuardadaRepository,
                                          UsuarioRepository usuarioRepository,
                                          PublicacionRepository publicacionRepository) {
        this.publicacionGuardadaRepository = publicacionGuardadaRepository;
        this.usuarioRepository = usuarioRepository;
        this.publicacionRepository = publicacionRepository;
    }

    @Override
    @Transactional
    public PublicacionGuardada guardarPublicacionGuardada(PublicacionGuardada publicacionGuardada) {
        if (publicacionGuardada.getFechaGuardado() == null) {
            publicacionGuardada.setFechaGuardado(Timestamp.from(Instant.now()));
        }
        return publicacionGuardadaRepository.save(publicacionGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PublicacionGuardada> obtenerPublicacionGuardadaPorId(PublicacionGuardadaId id) {
        return publicacionGuardadaRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublicacionGuardada> obtenerTodasLasPublicacionesGuardadas() {
        return publicacionGuardadaRepository.findAll();
    }

    @Override
    @Transactional
    public void eliminarPublicacionGuardada(PublicacionGuardadaId id) {
        publicacionGuardadaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublicacionGuardada> obtenerPublicacionesGuardadasPorUsuario(Long idUsuario) {
        return publicacionGuardadaRepository.findByIdUsuario(idUsuario);
    }

    public List<PublicacionGuardada> getPublicacionesGuardadasByUsuario(Long idUsuario) {
        // Asegúrate de que este método exista en tu repositorio
        return publicacionGuardadaRepository.findByUsuario_IdUsuario(idUsuario);
    }

    @Override
    @Transactional
    public PublicacionGuardada guardarPublicacionParaUsuario(Long idUsuario, Long idPublicacion) throws Exception {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));
        Publicacion publicacion = publicacionRepository.findById(idPublicacion)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada con ID: " + idPublicacion));

        PublicacionGuardadaId id = new PublicacionGuardadaId();
        id.setIdUsuario(idUsuario);
        id.setIdPublicacion(idPublicacion);

        if (publicacionGuardadaRepository.existsById(id)) {
            throw new RuntimeException("Esta publicación ya está guardada por este usuario.");
        }

        PublicacionGuardada pg = new PublicacionGuardada();
        pg.setIdUsuario(idUsuario);
        pg.setIdPublicacion(idPublicacion);
        pg.setUsuario(usuario);
        pg.setPublicacion(publicacion);
        pg.setFechaGuardado(Timestamp.from(Instant.now()));
        return publicacionGuardadaRepository.save(pg);
    }

    @Override
    @Transactional
    public void eliminarPublicacionGuardadaParaUsuario(Long idUsuario, Long idPublicacion) {
        PublicacionGuardadaId id = new PublicacionGuardadaId();
        id.setIdUsuario(idUsuario);
        id.setIdPublicacion(idPublicacion);
        publicacionGuardadaRepository.deleteById(id);
    }


}