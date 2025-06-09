package com.gestion.zarpas_backend.controlador;

import com.gestion.zarpas_backend.modelo.Comentario;
import com.gestion.zarpas_backend.servicio.ComentarioService;
import com.gestion.zarpas_backend.servicio.PublicacionService;
import com.gestion.zarpas_backend.servicio.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comentarios")
@CrossOrigin(origins = "http://localhost:4200")
public class ComentarioController {

    private final ComentarioService comentarioService;
    private final UsuarioService usuarioService;
    private final PublicacionService publicacionService;


    public static class ComentarioRequest {
        public Long idUsuario;
        public Long idPublicacion;
        public String texto;
    }
    @Autowired
    public ComentarioController(ComentarioService comentarioService, UsuarioService usuarioService, PublicacionService publicacionService) {
        this.comentarioService = comentarioService;
        this.usuarioService = usuarioService;
        this.publicacionService = publicacionService;
    }

    @PostMapping
    public ResponseEntity<?> crearComentario(@RequestBody ComentarioRequest request) {
        try {
            Comentario comentario = comentarioService.crearComentario(request.idUsuario, request.idPublicacion, request.texto);
            return new ResponseEntity<>(comentario, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor al crear comentario: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/publicacion/{idPublicacion}")
    public ResponseEntity<List<Comentario>> obtenerComentariosPorPublicacion(@PathVariable Long idPublicacion) {
        List<Comentario> comentarios = comentarioService.obtenerComentariosPorPublicacion(idPublicacion);
        return new ResponseEntity<>(comentarios, HttpStatus.OK);
    }

}
