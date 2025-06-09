package com.gestion.zarpas_backend.controlador;

import com.gestion.zarpas_backend.modelo.ComentarioReaccion;
import com.gestion.zarpas_backend.modelo.ComentarioReaccionId;
import com.gestion.zarpas_backend.modelo.TipoReaccion;
import com.gestion.zarpas_backend.servicio.ComentarioReaccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/reacciones-comentario")
@CrossOrigin(origins = "http://localhost:4200")
public class ComentarioReaccionController {

    @Autowired
    private ComentarioReaccionService comentarioReaccionService;

    public static class ComentarioReaccionRequest {
        public Long idUsuario;
        public Long idComentario;
        public TipoReaccion tipoReaccion;
    }

//
    @PutMapping("/toggle")
    public ResponseEntity<?> toggleReaccion(@RequestBody ComentarioReaccionRequest request) {
        try {
            ComentarioReaccion reaccionGuardada = comentarioReaccionService.crearOActualizarReaccion(
                    request.idUsuario, request.idComentario, request.tipoReaccion);

            if (reaccionGuardada == null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
            } else {
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
            @RequestParam TipoReaccion tipo) {
        try {
            ComentarioReaccion cr = comentarioReaccionService.reaccionarAComentario(idUsuario, idComentario, tipo);
            if (cr == null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(cr, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


//
    @GetMapping("/conteo/{idComentario}")
    public ResponseEntity<Map<TipoReaccion, Long>> getConteoReacciones(@PathVariable Long idComentario) {
        Map<TipoReaccion, Long> conteo = comentarioReaccionService.getConteoReaccionesByComentarioId(idComentario);
        conteo.putIfAbsent(TipoReaccion.like, 0L);
        conteo.putIfAbsent(TipoReaccion.dislike, 0L);
        return new ResponseEntity<>(conteo, HttpStatus.OK);
    }



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


//
    @GetMapping("/comentario/{idComentario}/usuario/{idUsuario}")
    public ResponseEntity<TipoReaccion> getReaccionByComentarioAndUsuario(
            @PathVariable Long idComentario,
            @PathVariable Long idUsuario) {
        Optional<ComentarioReaccion> reaccion = comentarioReaccionService.findByComentarioIdAndUsuarioId(idComentario, idUsuario);
        if (reaccion.isPresent()) {
            return ResponseEntity.ok(reaccion.get().getTipoReaccion());
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }
}