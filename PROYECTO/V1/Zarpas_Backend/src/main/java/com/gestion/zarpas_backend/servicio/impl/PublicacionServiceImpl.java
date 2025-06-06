package com.gestion.zarpas_backend.servicio.impl;

import com.gestion.zarpas_backend.modelo.Publicacion;
import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.repositorio.PublicacionRepository;
import com.gestion.zarpas_backend.servicio.PublicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class PublicacionServiceImpl implements PublicacionService {

    private final PublicacionRepository publicacionRepository;

    @Autowired
    public PublicacionServiceImpl(PublicacionRepository publicacionRepository) {
        this.publicacionRepository = publicacionRepository;
    }

    @Override
    @Transactional
    public Publicacion guardarPublicacion(Publicacion publicacion) {
        if (publicacion.getFechaCreacion() == null) {
            publicacion.setFechaCreacion(Timestamp.from(Instant.now()));
        }
        publicacion.setFechaModificacion(Timestamp.from(Instant.now()));
        return publicacionRepository.save(publicacion);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Publicacion> obtenerPublicacionPorId(Long id) {
        return publicacionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Publicacion> obtenerTodasLasPublicaciones() {
        System.out.println("PublicacionServiceImpl: Entrando en obtenerTodasLasPublicaciones()...");
        try {
            List<Publicacion> publicaciones = publicacionRepository.findAll();
            System.out.println("PublicacionServiceImpl: publicacionRepository.findAll() completado. Número de publicaciones: " + publicaciones.size());
            return publicaciones;
        } catch (Exception e) {
            System.err.println("PublicacionServiceImpl: ¡ERROR en obtenerTodasLasPublicaciones! " + e.getMessage());
            e.printStackTrace(); // Imprime el stack trace completo
            throw e; // Relanza la excepción para que el controlador la capture
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Publicacion> obtenerPublicacionesPorUsuario(Usuario usuario) {
        return publicacionRepository.findByUsuario(usuario);
    }

    @Override
    @Transactional
    public Publicacion actualizarPublicacion(Publicacion publicacion) {
        return publicacionRepository.findById(publicacion.getIdPublicacion())
                .map(existingPublicacion -> {
                    existingPublicacion.setTitulo(publicacion.getTitulo());
                    existingPublicacion.setContenido(publicacion.getContenido());
                    existingPublicacion.setImagenUrl(publicacion.getImagenUrl());
                    existingPublicacion.setFechaModificacion(Timestamp.from(Instant.now()));
                    return publicacionRepository.save(existingPublicacion);
                }).orElseThrow(() -> new RuntimeException("Publicación no encontrada con ID: " + publicacion.getIdPublicacion()));
    }

    @Override
    @Transactional
    public void eliminarPublicacion(Long id) {
        publicacionRepository.deleteById(id);
    }
}