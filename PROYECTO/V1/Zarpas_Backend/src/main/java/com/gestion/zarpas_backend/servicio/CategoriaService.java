// src/main/java/com/gestion/zarpas_backend/servicio/CategoriaService.java
package com.gestion.zarpas_backend.servicio;

import com.gestion.zarpas_backend.modelo.Categoria;
import java.util.List;
import java.util.Optional;

public interface CategoriaService {
    Categoria guardarCategoria(Categoria categoria);
    Optional<Categoria> obtenerCategoriaPorId(Long id);
    Optional<Categoria> obtenerCategoriaPorNombre(String nombre);
    List<Categoria> obtenerTodasLasCategorias();
    Categoria actualizarCategoria(Long id, String nuevoNombre, String nuevaDescripcion);
    void eliminarCategoria(Long id);
}