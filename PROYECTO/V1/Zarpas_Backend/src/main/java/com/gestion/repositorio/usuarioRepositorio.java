package com.gestion.repositorio;

import com.gestion.modelo.usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface usuarioRepositorio extends JpaRepository<usuario, Long> {
    
}
