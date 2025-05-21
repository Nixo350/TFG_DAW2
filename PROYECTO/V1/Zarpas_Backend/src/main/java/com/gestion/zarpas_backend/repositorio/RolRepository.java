package com.gestion.zarpas_backend.repositorio;

import com.gestion.zarpas_backend.modelo.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol,Long> {
    Optional<Rol> findByNombre(String nombre);
}
