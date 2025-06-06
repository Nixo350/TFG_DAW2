package com.gestion.zarpas_backend.controlador;

import com.gestion.zarpas_backend.dto.ReaccionPublicacionRequest;
import com.gestion.zarpas_backend.modelo.ReaccionPublicacion;
import com.gestion.zarpas_backend.modelo.TipoReaccion;
import com.gestion.zarpas_backend.request.ReaccionRequest;
import com.gestion.zarpas_backend.servicio.ReaccionPublicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/reacciones-publicacion")
@CrossOrigin(origins = "http://localhost:4200") // Asegúrate de que tu puerto Angular sea correcto
public class ReaccionPublicacionController {

    @Autowired
    private ReaccionPublicacionService reaccionPublicacionService;

    // Endpoint para crear o actualizar una reacción (like/dislike)
    // Cambiado a PUT y recibe un RequestBody con el DTO
    @PutMapping("/toggle") // <--- ¡CAMBIADO A PUT!
    public ResponseEntity<?> toggleReaccion(
            // @RequestParam Long idUsuario, // <--- ELIMINAR
            // @RequestParam Long idPublicacion, // <--- ELIMINAR
            // @RequestParam TipoReaccion tipoReaccion) { // <--- ELIMINAR
            @RequestBody ReaccionRequest request) { // <--- ¡AÑADIDO @RequestBody!
        try {
            ReaccionPublicacion reaccion = reaccionPublicacionService.crearOActualizarReaccion(
                    request.getIdUsuario(),
                    request.getIdPublicacion(),
                    request.getTipoReaccion()
            );
            if (reaccion == null) {
                return new ResponseEntity<>("Reacción eliminada.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>(reaccion, HttpStatus.OK);
            }
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para obtener el conteo de likes y dislikes de una publicación
    @GetMapping("/conteo/{idPublicacion}")
    public ResponseEntity<Map<TipoReaccion, Long>> getConteoReacciones(@PathVariable Long idPublicacion) {
        Map<TipoReaccion, Long> conteo = reaccionPublicacionService.getConteoReaccionesByPublicacionId(idPublicacion);
        // Asegurarse de que siempre devuelva 0 si no hay likes/dislikes, en lugar de no incluir la clave
        conteo.putIfAbsent(TipoReaccion.like, 0L);
        conteo.putIfAbsent(TipoReaccion.dislike, 0L);
        return new ResponseEntity<>(conteo, HttpStatus.OK);
    }

    // Endpoint para obtener la reacción de un usuario a una publicación específica
    @GetMapping("/usuario/{idUsuario}/publicacion/{idPublicacion}")
    public ResponseEntity<TipoReaccion> getReaccionUsuario(
            @PathVariable Long idUsuario,
            @PathVariable Long idPublicacion) {
        Optional<TipoReaccion> tipoReaccion = reaccionPublicacionService.getReaccionUsuarioAPublicacion(idUsuario, idPublicacion);
        return tipoReaccion.map(reaccion -> new ResponseEntity<>(reaccion, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}