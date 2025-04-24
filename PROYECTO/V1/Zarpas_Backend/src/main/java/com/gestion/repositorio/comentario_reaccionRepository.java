package com.gestion.repositorio;

import com.gestion.modelo.comentario_reaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface comentario_reaccionRepository extends JpaRepository<comentario_reaccion, Long> {
    
}
