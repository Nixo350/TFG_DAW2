package com.gestion.zarpas_backend.servicio.impl;

import com.gestion.zarpas_backend.modelo.Chat;
import com.gestion.zarpas_backend.repositorio.ChatRepository;
import com.gestion.zarpas_backend.servicio.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    @Autowired
    public ChatServiceImpl(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Override
    @Transactional
    public Chat guardarChat(Chat chat) {
        if (chat.getFechaCreacion() == null) {
            chat.setFechaCreacion(Timestamp.from(Instant.now()));
        }
        chat.setFechaModificacion(Timestamp.from(Instant.now()));
        return chatRepository.save(chat);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Chat> obtenerChatPorId(Long id) {
        return chatRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Chat> obtenerTodosLosChats() {
        return chatRepository.findAll();
    }

    @Override
    @Transactional
    public Chat actualizarChat(Chat chat) {
        return chatRepository.findById(chat.getIdChat())
                .map(existingChat -> {
                    existingChat.setNombre(chat.getNombre());
                    existingChat.setFechaModificacion(Timestamp.from(Instant.now()));
                    // Puedes añadir lógica para manejar usuarios del chat si es necesario
                    return chatRepository.save(existingChat);
                }).orElseThrow(() -> new RuntimeException("Chat no encontrado con ID: " + chat.getIdChat()));
    }

    @Override
    @Transactional
    public void eliminarChat(Long id) {
        chatRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Chat> obtenerChatsPorUsuarioId(Long idUsuario) {
        return chatRepository.findChatsByUsuarioId(idUsuario);
    }
}