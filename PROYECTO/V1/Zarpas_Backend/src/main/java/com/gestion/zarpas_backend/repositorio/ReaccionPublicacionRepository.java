package com.gestion.zarpas_backend.repositorio;

import com.gestion.zarpas_backend.modelo.Publicacion;
import com.gestion.zarpas_backend.modelo.ReaccionPublicacion;
import com.gestion.zarpas_backend.modelo.ReaccionPublicacionId;
import com.gestion.zarpas_backend.modelo.TipoReaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReaccionPublicacionRepository extends JpaRepository<ReaccionPublicacion, ReaccionPublicacionId> {

    // Encontrar una reacción específica de un usuario a una publicación
    Optional<ReaccionPublicacion> findById_IdUsuarioAndId_IdPublicacion(Long idUsuario, Long idPublicacion);

    // Contar el número de likes para una publicación específica
    long countByPublicacionAndTipoReaccion(Publicacion publicacion, TipoReaccion tipoReaccion);

    // Contar el número de likes para una publicación específica por ID
    long countByPublicacion_IdPublicacionAndTipoReaccion(Long idPublicacion, TipoReaccion tipoReaccion);

    // Encontrar todas las reacciones de una publicación (útil para el conteo)
    List<ReaccionPublicacion> findByPublicacion_IdPublicacion(Long idPublicacion);
}