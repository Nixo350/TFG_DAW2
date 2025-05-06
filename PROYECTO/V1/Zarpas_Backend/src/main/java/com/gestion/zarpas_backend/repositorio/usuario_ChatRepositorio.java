package com.gestion.zarpas_backend.repositorio;

import com.gestion.zarpas_backend.modelo.UsuarioChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface usuario_ChatRepositorio extends JpaRepository<UsuarioChat, Long> {

}
