package com.gestion.zarpas_backend.servicio;

import com.gestion.zarpas_backend.modelo.Mensaje;
import com.gestion.zarpas_backend.modelo.Chat;
import com.gestion.zarpas_backend.modelo.Usuario;
import java.util.List;
import java.util.Optional;

public interface MensajeService {
    Mensaje guardarMensaje(Mensaje mensaje);
    Optional<Mensaje> obtenerMensajePorId(Long id);
    List<Mensaje> obtenerTodosLosMensajes();
    List<Mensaje> obtenerMensajesPorChat(Chat chat);
    List<Mensaje> obtenerMensajesEnviadosPorUsuario(Usuario emisor);
    Mensaje actualizarMensaje(Mensaje mensaje);
    void eliminarMensaje(Long id);
    Mensaje marcarMensajeComoLeido(Long id);
}