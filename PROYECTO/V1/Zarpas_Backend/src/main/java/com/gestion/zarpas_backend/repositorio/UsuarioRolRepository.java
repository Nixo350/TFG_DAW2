package com.gestion.zarpas_backend.repositorio;

import com.gestion.zarpas_backend.modelo.UsuarioRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Long> {
    List<UsuarioRol> findByUsuario_IdUsuario(Long usuarioId);

    List<UsuarioRol> findByRol_Id(Long rolId);
}