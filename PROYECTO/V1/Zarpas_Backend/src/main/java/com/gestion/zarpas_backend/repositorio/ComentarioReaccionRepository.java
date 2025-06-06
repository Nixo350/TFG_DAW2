package com.gestion.zarpas_backend.repositorio;

import com.gestion.zarpas_backend.modelo.ComentarioReaccion;
import com.gestion.zarpas_backend.modelo.ComentarioReaccionId;
import com.gestion.zarpas_backend.modelo.TipoReaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComentarioReaccionRepository extends JpaRepository<ComentarioReaccion, ComentarioReaccionId> {
    List<ComentarioReaccion> findById_IdComentario(Long idComentario);
    List<ComentarioReaccion> findById_IdUsuario(Long idUsuario);



    Optional<ComentarioReaccion> findById_IdUsuarioAndId_IdComentario(Long idUsuario, Long idComentario);

    List<ComentarioReaccion> findByComentario_IdComentario(Long idComentario);

    long countByComentario_IdComentarioAndTipoReaccion(Long idComentario, TipoReaccion tipoReaccion);

}
