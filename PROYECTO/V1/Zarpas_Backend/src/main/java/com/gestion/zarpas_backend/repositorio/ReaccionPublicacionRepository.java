package com.gestion.zarpas_backend.repositorio;

import com.gestion.zarpas_backend.modelo.ReaccionPublicacion;
import com.gestion.zarpas_backend.modelo.ReaccionPublicacionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReaccionPublicacionRepository extends JpaRepository<ReaccionPublicacion, ReaccionPublicacionId> {

    Optional<ReaccionPublicacion> findById_IdUsuarioAndId_IdPublicacion(Long idUsuario, Long idPublicacion);

    List<ReaccionPublicacion> findByPublicacion_IdPublicacion(Long idPublicacion);

    Optional<ReaccionPublicacion> findByPublicacion_IdPublicacionAndUsuario_IdUsuario(Long idPublicacion, Long idUsuario);
}