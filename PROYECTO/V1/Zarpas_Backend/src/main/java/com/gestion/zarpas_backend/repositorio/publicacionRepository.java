package com.gestion.zarpas_backend.repositorio;

import com.gestion.zarpas_backend.modelo.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface publicacionRepository extends JpaRepository<Publicacion, Long> {

}
