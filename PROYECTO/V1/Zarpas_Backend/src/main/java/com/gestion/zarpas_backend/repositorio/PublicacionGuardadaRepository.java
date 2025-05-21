package com.gestion.zarpas_backend.repositorio;

import com.gestion.zarpas_backend.modelo.PublicacionGuardada;
import com.gestion.zarpas_backend.modelo.PublicacionGuardadaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicacionGuardadaRepository extends JpaRepository<PublicacionGuardada, PublicacionGuardadaId> {
    List<PublicacionGuardada> findByIdUsuario(Long idUsuario);
}
