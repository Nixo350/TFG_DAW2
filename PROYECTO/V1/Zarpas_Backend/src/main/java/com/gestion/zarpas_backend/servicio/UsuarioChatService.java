package com.gestion.zarpas_backend.servicio;

import com.gestion.zarpas_backend.modelo.UsuarioChat;
import com.gestion.zarpas_backend.modelo.UsuarioChatId;
import java.util.List;
import java.util.Optional;

public interface UsuarioChatService {
    UsuarioChat guardarUsuarioChat(UsuarioChat usuarioChat);
    Optional<UsuarioChat> obtenerUsuarioChatPorId(UsuarioChatId id);
    List<UsuarioChat> obtenerTodosLosUsuarioChats();
    void eliminarUsuarioChat(UsuarioChatId id);
    UsuarioChat unirUsuarioAchat(Long idUsuario, Long idChat) throws Exception;
    void sacarUsuarioDeChat(Long idUsuario, Long idChat);
    List<UsuarioChat> obtenerChatsDeUsuario(Long idUsuario);
    List<UsuarioChat> obtenerUsuariosDeChat(Long idChat);
}