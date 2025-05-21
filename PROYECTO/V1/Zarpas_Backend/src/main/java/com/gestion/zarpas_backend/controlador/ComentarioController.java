package com.gestion.zarpas_backend.controlador;

import com.gestion.zarpas_backend.modelo.Comentario;
import com.gestion.zarpas_backend.modelo.Publicacion;
import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.servicio.ComentarioService;
import com.gestion.zarpas_backend.servicio.PublicacionService;
import com.gestion.zarpas_backend.servicio.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comentarios")
public class ComentarioController {

    private final ComentarioService comentarioService;
    private final UsuarioService usuarioService; // Para asociar usuario
    private final PublicacionService publicacionService; // Para asociar publicación

    @Autowired
    public ComentarioController(ComentarioService comentarioService, UsuarioService usuarioService, PublicacionService publicacionService) {
        this.comentarioService = comentarioService;
        this.usuarioService = usuarioService;
        this.publicacionService = publicacionService;
    }

    @PostMapping
    public ResponseEntity<Comentario> crearComentario(@RequestBody Comentario comentario) {
        if (comentario.getUsuario() == null || comentario.getUsuario().getIdUsuario() == null ||
                comentario.getPublicacion() == null || comentario.getPublicacion().getIdPublicacion() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Se requieren IDs de usuario y publicación
        }

        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorId(comentario.getUsuario().getIdUsuario());
        Optional<Publicacion> publicacionOpt = publicacionService.obtenerPublicacionPorId(comentario.getPublicacion().getIdPublicacion());

        if (usuarioOpt.isPresent() && publicacionOpt.isPresent()) {
            comentario.setUsuario(usuarioOpt.get());
            comentario.setPublicacion(publicacionOpt.get());
            Comentario nuevoComentario = comentarioService.guardarComentario(comentario);
            return new ResponseEntity<>(nuevoComentario, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Usuario o Publicación no encontrados
        }
    }

    @GetMapping
    public ResponseEntity<List<Comentario>> obtenerTodosLosComentarios() {
        List<Comentario> comentarios = comentarioService.obtenerTodosLosComentarios();
        return new ResponseEntity<>(comentarios, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comentario> obtenerComentarioPorId(@PathVariable("id") Long id) {
        return comentarioService.obtenerComentarioPorId(id)
                .map(comentario -> new ResponseEntity<>(comentario, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/publicacion/{publicacionId}")
    public ResponseEntity<List<Comentario>> obtenerComentariosPorPublicacion(@PathVariable Long publicacionId) {
        return publicacionService.obtenerPublicacionPorId(publicacionId)
                .map(publicacion -> {
                    List<Comentario> comentarios = comentarioService.obtenerComentariosPorPublicacion(publicacion);
                    return new ResponseEntity<>(comentarios, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Comentario>> obtenerComentariosPorUsuario(@PathVariable Long usuarioId) {
        return usuarioService.obtenerUsuarioPorId(usuarioId)
                .map(usuario -> {
                    List<Comentario> comentarios = comentarioService.obtenerComentariosPorUsuario(usuario);
                    return new ResponseEntity<>(comentarios, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comentario> actualizarComentario(@PathVariable("id") Long id, @RequestBody Comentario comentario) {
        if (!id.equals(comentario.getIdComentario())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            Comentario comentarioActualizado = comentarioService.actualizarComentario(comentario);
            return new ResponseEntity<>(comentarioActualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarComentario(@PathVariable("id") Long id) {
        try {
            comentarioService.eliminarComentario(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}