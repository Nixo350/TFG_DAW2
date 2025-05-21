package com.gestion.zarpas_backend.servicio.impl;

import com.gestion.zarpas_backend.modelo.Chat;
import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.modelo.UsuarioChat;
import com.gestion.zarpas_backend.modelo.UsuarioChatId;
import com.gestion.zarpas_backend.repositorio.UsuarioChatRepository;
import com.gestion.zarpas_backend.repositorio.UsuarioRepository;
import com.gestion.zarpas_backend.repositorio.ChatRepository;
import com.gestion.zarpas_backend.servicio.UsuarioChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioChatServiceImpl implements UsuarioChatService {

    private final UsuarioChatRepository usuarioChatRepository;
    private final UsuarioRepository usuarioRepository;
    private final ChatRepository chatRepository;

    @Autowired
    public UsuarioChatServiceImpl(UsuarioChatRepository usuarioChatRepository,
                                  UsuarioRepository usuarioRepository,
                                  ChatRepository chatRepository) {
        this.usuarioChatRepository = usuarioChatRepository;
        this.usuarioRepository = usuarioRepository;
        this.chatRepository = chatRepository;
    }

    @Override
    @Transactional
    public UsuarioChat guardarUsuarioChat(UsuarioChat usuarioChat) {
        if (usuarioChat.getFechaUnion() == null) {
            usuarioChat.setFechaUnion(Timestamp.from(Instant.now()));
        }
        return usuarioChatRepository.save(usuarioChat);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioChat> obtenerUsuarioChatPorId(UsuarioChatId id) {
        return usuarioChatRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioChat> obtenerTodosLosUsuarioChats() {
        return usuarioChatRepository.findAll();
    }

    @Override
    @Transactional
    public void eliminarUsuarioChat(UsuarioChatId id) {
        usuarioChatRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UsuarioChat unirUsuarioAchat(Long idUsuario, Long idChat) throws Exception {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));
        Chat chat = chatRepository.findById(idChat)
                .orElseThrow(() -> new RuntimeException("Chat no encontrado con ID: " + idChat));

        UsuarioChatId id = new UsuarioChatId();
        id.setIdUsuario(idUsuario);
        id.setIdChat(idChat);

        if (usuarioChatRepository.existsById(id)) {
            throw new RuntimeException("El usuario ya es miembro de este chat.");
        }

        UsuarioChat uc = new UsuarioChat();
        uc.setIdUsuario(idUsuario);
        uc.setIdChat(idChat);
        uc.setUsuario(usuario);
        uc.setChat(chat);
        uc.setFechaUnion(Timestamp.from(Instant.now()));
        return usuarioChatRepository.save(uc);
    }

    @Override
    @Transactional
    public void sacarUsuarioDeChat(Long idUsuario, Long idChat) {
        UsuarioChatId id = new UsuarioChatId();
        id.setIdUsuario(idUsuario);
        id.setIdChat(idChat);
        usuarioChatRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioChat> obtenerChatsDeUsuario(Long idUsuario) {
        return usuarioChatRepository.findByIdUsuario(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioChat> obtenerUsuariosDeChat(Long idChat) {
        return usuarioChatRepository.findByIdChat(idChat);
    }
}