package com.gestion.zarpas_backend.repositorio;

import com.gestion.zarpas_backend.modelo.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface comentarioRepository extends JpaRepository<Comentario, Long> {

}
