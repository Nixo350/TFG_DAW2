package com.gestion.zarpas_backend.servicio;

import com.gestion.zarpas_backend.modelo.Chat;
import java.util.List;
import java.util.Optional;

public interface ChatService {
    Chat guardarChat(Chat chat);
    Optional<Chat> obtenerChatPorId(Long id);
    List<Chat> obtenerTodosLosChats();
    Chat actualizarChat(Chat chat);
    void eliminarChat(Long id);
    List<Chat> obtenerChatsPorUsuarioId(Long idUsuario); // Ajustado para recibir ID de usuario
}