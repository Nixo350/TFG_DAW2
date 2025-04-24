package com.gestion.repositorio;

import com.gestion.modelo.usuario_chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface usuario_ChatRepositorio extends JpaRepository<usuario_chat, Long> {
    
}
