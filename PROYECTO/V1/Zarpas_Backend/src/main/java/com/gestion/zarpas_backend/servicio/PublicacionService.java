package com.gestion.zarpas_backend.servicio;

import com.gestion.zarpas_backend.modelo.Categoria;
import com.gestion.zarpas_backend.modelo.Publicacion;
import com.gestion.zarpas_backend.modelo.Usuario;
import java.util.List;
import java.util.Optional;

public interface PublicacionService {
    Publicacion guardarPublicacion(Publicacion publicacion);
    Optional<Publicacion> obtenerPublicacionPorId(Long id);
    List<Publicacion> obtenerTodasLasPublicaciones();
    List<Publicacion> obtenerPublicacionesPorUsuario(Usuario usuario);
    Publicacion actualizarPublicacion(Publicacion publicacion);
    void eliminarPublicacion(Long id);
    List<Publicacion> searchPublicaciones(String keyword);

    List<Categoria> getAllCategorias();
    List<Publicacion> getPublicacionesByCategoria(String nombreCategoria);
    Categoria crearCategoria(Categoria categoria);
    Optional<Categoria> getCategoriaById(Long id);
}