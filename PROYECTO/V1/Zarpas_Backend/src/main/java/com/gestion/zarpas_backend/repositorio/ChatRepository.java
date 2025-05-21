package com.gestion.zarpas_backend.repositorio;

import com.gestion.zarpas_backend.modelo.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("SELECT uc.chat FROM UsuarioChat uc WHERE uc.usuario.idUsuario = :idUsuario")
    List<Chat> findChatsByUsuarioId(Long idUsuario);
}
