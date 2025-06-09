package com.gestion.zarpas_backend.servicio;

import com.gestion.zarpas_backend.modelo.Comentario;
import java.util.List;

public interface ComentarioService {

    List<Comentario> obtenerComentariosPorPublicacion(Long idPublicacion);

    Comentario crearComentario(Long idUsuario, Long idPublicacion, String texto);


}