package com.gestion.zarpas_backend.repositorio;

import com.gestion.zarpas_backend.modelo.UsuarioChat;
import com.gestion.zarpas_backend.modelo.UsuarioChatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioChatRepository extends JpaRepository<UsuarioChat, UsuarioChatId> {
    List<UsuarioChat> findByIdUsuario(Long idUsuario);
    List<UsuarioChat> findByIdChat(Long idChat);
}
