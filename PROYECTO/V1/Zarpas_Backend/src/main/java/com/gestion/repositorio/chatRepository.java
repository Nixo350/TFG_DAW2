package com.gestion.repositorio;

import com.gestion.modelo.chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface chatRepository extends JpaRepository<chat, Long> {
    
}
