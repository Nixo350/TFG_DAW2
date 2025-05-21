package com.gestion.zarpas_backend.repositorio;

import com.gestion.zarpas_backend.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    public Usuario findBynombre(String nombre);
    Optional<Usuario> findByEmail(String email);

}
