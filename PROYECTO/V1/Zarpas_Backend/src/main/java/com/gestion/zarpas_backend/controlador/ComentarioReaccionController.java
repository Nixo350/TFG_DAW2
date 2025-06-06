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
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/reacciones-comentario") // Nueva ruta base
@CrossOrigin(origins = "http://localhost:4200")
public class ComentarioReaccionController {

    @Autowired
    private ComentarioReaccionService comentarioReaccionService;

    public static class ComentarioReaccionRequest {
        public Long idUsuario;
        public Long idComentario;
        public TipoReaccion tipoReaccion;
    }

    @PutMapping("/toggle")
    public ResponseEntity<?> toggleReaccion(@RequestBody ComentarioReaccionRequest request) {
        try {
            ComentarioReaccion reaccionGuardada = comentarioReaccionService.crearOActualizarReaccion(
                    request.idUsuario, request.idComentario, request.tipoReaccion);

            if (reaccionGuardada == null) {
                // Reacción eliminada (toggle off)
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
            } else {
                // Reacción creada o actualizada
                return new ResponseEntity<>(reaccionGuardada, HttpStatus.OK); // 200 OK con el objeto de reacción
            }
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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

    @GetMapping("/conteo/{idComentario}")
    public ResponseEntity<Map<TipoReaccion, Long>> getConteoReacciones(@PathVariable Long idComentario) {
        Map<TipoReaccion, Long> conteo = comentarioReaccionService.getConteoReaccionesByComentarioId(idComentario);
        // Asegurarse de que siempre devuelva 0 si no hay likes/dislikes, en lugar de no incluir la clave
        conteo.putIfAbsent(TipoReaccion.like, 0L);
        conteo.putIfAbsent(TipoReaccion.dislike, 0L);
        return new ResponseEntity<>(conteo, HttpStatus.OK);
    }

    @GetMapping("/usuario/{idUsuario}/comentario/{idComentario}")
    public ResponseEntity<TipoReaccion> getReaccionUsuario(
            @PathVariable Long idUsuario,
            @PathVariable Long idComentario) {
        Optional<TipoReaccion> tipoReaccion = comentarioReaccionService.getReaccionUsuarioAComentario(idUsuario, idComentario);
        return tipoReaccion.map(reaccion -> new ResponseEntity<>(reaccion, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // 404 si no hay reacción
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