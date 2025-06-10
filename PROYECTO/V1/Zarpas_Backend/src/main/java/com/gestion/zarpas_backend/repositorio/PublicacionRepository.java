package com.gestion.zarpas_backend.repositorio;

import com.gestion.zarpas_backend.modelo.Publicacion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {
    @Query("SELECT p FROM Publicacion p WHERE " +
            "LOWER(p.titulo) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.contenido) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.usuario.username) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Publicacion> searchByKeyword(@Param("keyword") String keyword);
    List<Publicacion> findByCategoria_NombreIgnoreCaseOrderByFechaCreacionDesc(String nombre);
    @EntityGraph(attributePaths = "usuario")
    List<Publicacion> findAllByOrderByFechaCreacionDesc();

    List<Publicacion> findByUsuarioIdUsuario(Long userId);

}
