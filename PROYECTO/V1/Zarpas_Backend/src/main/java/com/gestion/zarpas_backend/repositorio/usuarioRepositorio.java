package com.gestion.zarpas_backend.repositorio;

import com.gestion.zarpas_backend.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface usuarioRepositorio extends JpaRepository<Usuario, Long> {

}
