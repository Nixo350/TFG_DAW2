package com.gestion.zarpas_backend.repositorio;

import com.gestion.zarpas_backend.modelo.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface mensajeRepository extends JpaRepository<Mensaje, Long> {

}
