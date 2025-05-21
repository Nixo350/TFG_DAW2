package com.gestion.zarpas_backend.controlador;

import com.gestion.zarpas_backend.modelo.Publicacion;
import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.servicio.PublicacionService;
import com.gestion.zarpas_backend.servicio.UsuarioService; // Para buscar el usuario por ID
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publicaciones")
public class PublicacionController {

    private final PublicacionService publicacionService;
    private final UsuarioService usuarioService; // Necesario para asociar publicaciones a usuarios existentes

    @Autowired
    public PublicacionController(PublicacionService publicacionService, UsuarioService usuarioService) {
        this.publicacionService = publicacionService;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<Publicacion> crearPublicacion(@RequestBody Publicacion publicacion) {
        // Asegurarse de que el usuario asociado a la publicación exista
        if (publicacion.getUsuario() == null || publicacion.getUsuario().getIdUsuario() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Se requiere ID de usuario
        }
        return usuarioService.obtenerUsuarioPorId(publicacion.getUsuario().getIdUsuario())
                .map(usuario -> {
                    publicacion.setUsuario(usuario); // Asignar el usuario manejado por JPA
                    Publicacion nuevaPublicacion = publicacionService.guardarPublicacion(publicacion);
                    return new ResponseEntity<>(nuevaPublicacion, HttpStatus.CREATED);
                })
                .orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST)); // Usuario no encontrado
    }

    @GetMapping
    public ResponseEntity<List<Publicacion>> obtenerTodasLasPublicaciones() {
        List<Publicacion> publicaciones = publicacionService.obtenerTodasLasPublicaciones();
        return new ResponseEntity<>(publicaciones, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Publicacion> obtenerPublicacionPorId(@PathVariable("id") Long id) {
        return publicacionService.obtenerPublicacionPorId(id)
                .map(publicacion -> new ResponseEntity<>(publicacion, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Publicacion>> obtenerPublicacionesPorUsuario(@PathVariable Long usuarioId) {
        return usuarioService.obtenerUsuarioPorId(usuarioId)
                .map(usuario -> {
                    List<Publicacion> publicaciones = publicacionService.obtenerPublicacionesPorUsuario(usuario);
                    return new ResponseEntity<>(publicaciones, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Publicacion> actualizarPublicacion(@PathVariable("id") Long id, @RequestBody Publicacion publicacion) {
        if (!id.equals(publicacion.getIdPublicacion())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            Publicacion publicacionActualizada = publicacionService.actualizarPublicacion(publicacion);
            return new ResponseEntity<>(publicacionActualizada, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPublicacion(@PathVariable("id") Long id) {
        try {
            publicacionService.eliminarPublicacion(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}