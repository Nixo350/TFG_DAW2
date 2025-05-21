package com.gestion.zarpas_backend.servicio.impl;

import com.gestion.zarpas_backend.modelo.Mensaje;
import com.gestion.zarpas_backend.modelo.Chat;
import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.repositorio.MensajeRepository;
import com.gestion.zarpas_backend.servicio.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class MensajeServiceImpl implements MensajeService {

    private final MensajeRepository mensajeRepository;

    @Autowired
    public MensajeServiceImpl(MensajeRepository mensajeRepository) {
        this.mensajeRepository = mensajeRepository;
    }

    @Override
    @Transactional
    public Mensaje guardarMensaje(Mensaje mensaje) {
        if (mensaje.getFechaEnvio() == null) {
            mensaje.setFechaEnvio(Timestamp.from(Instant.now()));
        }
        if (mensaje.getLeido() == null) {
            mensaje.setLeido(false);
        }
        return mensajeRepository.save(mensaje);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Mensaje> obtenerMensajePorId(Long id) {
        return mensajeRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Mensaje> obtenerTodosLosMensajes() {
        return mensajeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Mensaje> obtenerMensajesPorChat(Chat chat) {
        return mensajeRepository.findByChat(chat);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Mensaje> obtenerMensajesEnviadosPorUsuario(Usuario emisor) {
        return mensajeRepository.findByEmisor(emisor);
    }

    @Override
    @Transactional
    public Mensaje actualizarMensaje(Mensaje mensaje) {
        return mensajeRepository.findById(mensaje.getIdMensaje())
                .map(existingMensaje -> {
                    existingMensaje.setContenido(mensaje.getContenido());
                    existingMensaje.setLeido(mensaje.getLeido()); // Permitir actualizar el estado de lectura
                    return mensajeRepository.save(existingMensaje);
                }).orElseThrow(() -> new RuntimeException("Mensaje no encontrado con ID: " + mensaje.getIdMensaje()));
    }

    @Override
    @Transactional
    public void eliminarMensaje(Long id) {
        mensajeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Mensaje marcarMensajeComoLeido(Long id) {
        return mensajeRepository.findById(id).map(mensaje -> {
            mensaje.setLeido(true);
            return mensajeRepository.save(mensaje);
        }).orElseThrow(() -> new RuntimeException("Mensaje no encontrado con ID: " + id));
    }
}