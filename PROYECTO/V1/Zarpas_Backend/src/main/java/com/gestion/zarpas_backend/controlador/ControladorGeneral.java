package com.gestion.zarpas_backend.controlador;

import com.gestion.zarpas_backend.modelo.*;
import com.gestion.zarpas_backend.repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/apis/")
public class ControladorGeneral {
//Clase encargada ppara pruebas no implementacion de la app
    @Autowired
    private UsuarioRepository repositorioUsuario;

    @GetMapping("/usuarios")
    public List<Usuario> obtenerUsuarios() {
        return repositorioUsuario.findAll();
    }

    @Autowired
    private UsuarioChatRepository repositorioUsuarioChat;
    @GetMapping("/usuario_Chats")
    public List<UsuarioChat> obtenerUsuarioChats() {
        return repositorioUsuarioChat.findAll();
    }

    @Autowired
    private PublicacionRepository repositorioPublicacion;
    @GetMapping("/publicaciones")
    public List<Publicacion> obtenerPublicaciones() {
        return repositorioPublicacion.findAll();
    }

    @Autowired
    private PublicacionGuardadaRepository repositorioPublicacionGuardada;
    @GetMapping("/pubicaciones_guardadas")
    public List<PublicacionGuardada> obtenerPublicacionesGuardadas() {
        return repositorioPublicacionGuardada.findAll();
    }

    @Autowired
    private MensajeRepository repositorioMensaje;
    @GetMapping("/mensajes")
    public List<Mensaje> obtenerMensajes() {
        return repositorioMensaje.findAll();
    }

    @Autowired
    private ComentarioRepository repositorioComentario;
    @GetMapping("/comentarios")
    public List<Comentario> obtenerComentarios() {
        return repositorioComentario.findAll();
    }

    @Autowired
    private ComentarioReaccionRepository repositorioComentarioReaccion;
    @GetMapping("/comentario_reacciones")
    public List<ComentarioReaccion> obtenerComentarioReacciones() {
        return repositorioComentarioReaccion.findAll();
    }

    @Autowired
    private ChatRepository repositorioChat;
    @GetMapping("/chats")
    public List<Chat> ObtenerChats() {
        return repositorioChat.findAll();
    }

    @Autowired
    private RolRepository repositorioRol;
    @GetMapping("/roles")
    public List<Rol> ObtenerRoles() {
        return repositorioRol.findAll();
    }



}
