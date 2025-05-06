package com.gestion.zarpas_backend.repositorio;

import com.gestion.zarpas_backend.modelo.PublicacionGuardada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface publicacion_guardadaRepository extends JpaRepository<PublicacionGuardada, Long> {

}
