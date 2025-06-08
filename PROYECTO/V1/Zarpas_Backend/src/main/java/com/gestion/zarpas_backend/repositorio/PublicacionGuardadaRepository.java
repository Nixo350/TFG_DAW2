package com.gestion.zarpas_backend.repositorio;

import com.gestion.zarpas_backend.modelo.PublicacionGuardada;
import com.gestion.zarpas_backend.modelo.PublicacionGuardadaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicacionGuardadaRepository extends JpaRepository<PublicacionGuardada, PublicacionGuardadaId> {
    List<PublicacionGuardada> findByIdUsuario(Long idUsuario);
    boolean existsByPublicacionIdPublicacionAndUsuarioIdUsuario(Long idPublicacion, Long idUsuario);
    void deleteByPublicacionIdPublicacionAndUsuarioIdUsuario(Long idPublicacion, Long idUsuario);
    @Query("SELECT pg FROM PublicacionGuardada pg JOIN FETCH pg.publicacion p JOIN FETCH pg.usuario u WHERE pg.usuario.idUsuario = :idUsuario")
    List<PublicacionGuardada> findByUsuario_IdUsuario(Long idUsuario);

}
