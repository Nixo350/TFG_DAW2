package com.gestion.zarpas_backend.repositorio;

import com.gestion.zarpas_backend.modelo.Comentario;
import com.gestion.zarpas_backend.modelo.Publicacion;
import com.gestion.zarpas_backend.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByPublicacion(Publicacion publicacion);
    List<Comentario> findByUsuario(Usuario usuario);

}
