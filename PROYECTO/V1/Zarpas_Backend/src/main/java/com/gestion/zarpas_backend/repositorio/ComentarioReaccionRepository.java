package com.gestion.zarpas_backend.repositorio;

import com.gestion.zarpas_backend.modelo.ComentarioReaccion;
import com.gestion.zarpas_backend.modelo.ComentarioReaccionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioReaccionRepository extends JpaRepository<ComentarioReaccion, ComentarioReaccionId> {
    List<ComentarioReaccion> findByIdComentario(Long idComentario);
    List<ComentarioReaccion> findByIdUsuario(Long idUsuario);
}
