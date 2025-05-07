package com.gestion.zarpas_backend.controlador;

import com.gestion.zarpas_backend.modelo.*;
import com.gestion.zarpas_backend.repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/")
public class ControladorGeneral {

    @Autowired
    private usuarioRepositorio repositorioUsuario;

    @GetMapping("/usuarios")
    public List<Usuario> obtenerUsuarios() {
        return repositorioUsuario.findAll();
    }

    @Autowired
    private usuario_ChatRepositorio repositorioUsuarioChat;
    @GetMapping("/usuario_Chats")
    public List<UsuarioChat> obtenerUsuarioChats() {
        return repositorioUsuarioChat.findAll();
    }

    @Autowired
    private publicacionRepository repositorioPublicacion;
    @GetMapping("/publicaciones")
    public List<Publicacion> obtenerPublicaciones() {
        return repositorioPublicacion.findAll();
    }

    @Autowired
    private publicacion_guardadaRepository repositorioPublicacionGuardada;
    @GetMapping("/pubicaciones_guardadas")
    public List<PublicacionGuardada> obtenerPublicacionesGuardadas() {
        return repositorioPublicacionGuardada.findAll();
    }

    @Autowired
    private mensajeRepository repositorioMensaje;
    @GetMapping("/mensajes")
    public List<Mensaje> obtenerMensajes() {
        return repositorioMensaje.findAll();
    }

    @Autowired
    private comentarioRepository repositorioComentario;
    @GetMapping("/comentarios")
    public List<Comentario> obtenerComentarios() {
        return repositorioComentario.findAll();
    }

    @Autowired
    private comentario_reaccionRepository repositorioComentarioReaccion;
    @GetMapping("/comentario_reacciones")
    public List<ComentarioReaccion> obtenerComentarioReacciones() {
        return repositorioComentarioReaccion.findAll();
    }

    @Autowired
    private chatRepository repositorioChat;
    @GetMapping("/chats")
    public List<Chat> ObtenerChats() {
        return repositorioChat.findAll();
    }

    @Autowired
    private rolRepositorio repositorioRol;
    @GetMapping("/roles")
    public List<Rol> ObtenerRoles() {
        return repositorioRol.findAll();
    }



}
