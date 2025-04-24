package com.gestion.repositorio;

import com.gestion.modelo.publicacion_guardada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface publicacion_guardadaRepository extends JpaRepository<publicacion_guardada, Long> {
    
}
