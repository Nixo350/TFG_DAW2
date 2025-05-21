package com.gestion.zarpas_backend.controlador;

import com.gestion.zarpas_backend.modelo.ComentarioReaccion;
import com.gestion.zarpas_backend.modelo.ComentarioReaccionId;
import com.gestion.zarpas_backend.modelo.TipoReaccion;
import com.gestion.zarpas_backend.servicio.ComentarioReaccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comentario-reacciones")
public class ComentarioReaccionController {

    private final ComentarioReaccionService comentarioReaccionService;

    @Autowired
    public ComentarioReaccionController(ComentarioReaccionService comentarioReaccionService) {
        this.comentarioReaccionService = comentarioReaccionService;
    }

    @PostMapping("/usuario/{idUsuario}/comentario/{idComentario}/reaccionar")
    public ResponseEntity<ComentarioReaccion> reaccionarAComentario(
            @PathVariable Long idUsuario,
            @PathVariable Long idComentario,
            @RequestParam TipoReaccion tipo) { // Usamos @RequestParam para el tipo de reacción
        try {
            ComentarioReaccion cr = comentarioReaccionService.reaccionarAComentario(idUsuario, idComentario, tipo);
            if (cr == null) {
                // Si el servicio devuelve null, significa que se eliminó la reacción existente
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(cr, HttpStatus.OK); // O HttpStatus.CREATED si siempre se crea una nueva
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<ComentarioReaccion>> obtenerTodasLasComentarioReacciones() {
        List<ComentarioReaccion> crs = comentarioReaccionService.obtenerTodasLasComentarioReacciones();
        return new ResponseEntity<>(crs, HttpStatus.OK);
    }

    @GetMapping("/comentario/{idComentario}")
    public ResponseEntity<List<ComentarioReaccion>> obtenerReaccionesPorComentario(@PathVariable Long idComentario) {
        List<ComentarioReaccion> crs = comentarioReaccionService.obtenerReaccionesPorComentario(idComentario);
        return new ResponseEntity<>(crs, HttpStatus.OK);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ComentarioReaccion>> obtenerReaccionesPorUsuario(@PathVariable Long idUsuario) {
        List<ComentarioReaccion> crs = comentarioReaccionService.obtenerReaccionesPorUsuario(idUsuario);
        return new ResponseEntity<>(crs, HttpStatus.OK);
    }

    @DeleteMapping("/usuario/{idUsuario}/comentario/{idComentario}")
    public ResponseEntity<Void> eliminarReaccionComentario(
            @PathVariable Long idUsuario, @PathVariable Long idComentario) {
        try {
            comentarioReaccionService.eliminarReaccionComentario(idUsuario, idComentario);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Opcional: Endpoint para obtener por el ID compuesto directamente (menos común)
    @GetMapping("/{idUsuario}/{idComentario}")
    public ResponseEntity<ComentarioReaccion> obtenerComentarioReaccionPorIds(
            @PathVariable Long idUsuario, @PathVariable Long idComentario) {
        ComentarioReaccionId id = new ComentarioReaccionId();
        id.setIdUsuario(idUsuario);
        id.setIdComentario(idComentario);
        return comentarioReaccionService.obtenerComentarioReaccionPorId(id)
                .map(cr -> new ResponseEntity<>(cr, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}