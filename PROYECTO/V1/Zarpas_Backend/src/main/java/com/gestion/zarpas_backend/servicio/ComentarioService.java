package com.gestion.zarpas_backend.servicio;

import com.gestion.zarpas_backend.modelo.Comentario;
import com.gestion.zarpas_backend.modelo.Publicacion;
import com.gestion.zarpas_backend.modelo.Usuario;
import java.util.List;
import java.util.Optional;

public interface ComentarioService {
    Comentario guardarComentario(Comentario comentario);
    Optional<Comentario> obtenerComentarioPorId(Long id);
    List<Comentario> obtenerTodosLosComentarios();
    List<Comentario> obtenerComentariosPorPublicacion(Publicacion publicacion);
    List<Comentario> obtenerComentariosPorUsuario(Usuario usuario);
    Comentario actualizarComentario(Comentario comentario);
    void eliminarComentario(Long id);
}