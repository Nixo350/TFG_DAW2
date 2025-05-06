package com.gestion.zarpas_backend.repositorio;

import com.gestion.zarpas_backend.modelo.ComentarioReaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface comentario_reaccionRepository extends JpaRepository<ComentarioReaccion, Long> {

}
