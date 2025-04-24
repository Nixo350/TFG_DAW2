package com.gestion.repositorio;

import com.gestion.modelo.mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface mensajeRepository extends JpaRepository<mensaje, Long> {
    
}
