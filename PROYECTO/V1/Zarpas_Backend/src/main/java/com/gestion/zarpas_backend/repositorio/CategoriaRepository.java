// src/main/java/com/gestion/zarpas_backend/repositorio/CategoriaRepository.java
package com.gestion.zarpas_backend.repositorio;

import com.gestion.zarpas_backend.modelo.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNombre(String nombre);
}