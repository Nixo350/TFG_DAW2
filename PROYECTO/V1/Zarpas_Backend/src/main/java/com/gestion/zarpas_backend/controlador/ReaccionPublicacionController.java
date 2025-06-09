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
@CrossOrigin(origins = "http://localhost:4200")
public class ReaccionPublicacionController {

    @Autowired
    private ReaccionPublicacionService reaccionPublicacionService;
//Metodo para reaacionar a publicaciones
    @PutMapping("/toggle")
    public ResponseEntity<?> toggleReaccion(@RequestBody ReaccionRequest request) {
        try {
            ReaccionPublicacion reaccionGuardada = reaccionPublicacionService.crearOActualizarReaccion(
                    request.getIdUsuario(), request.getIdPublicacion(), request.getTipoReaccion());

            if (reaccionGuardada != null) {
                return new ResponseEntity<>(reaccionGuardada, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/conteo/{idPublicacion}")
    public ResponseEntity<Map<TipoReaccion, Long>> getConteoReacciones(@PathVariable Long idPublicacion) {
        Map<TipoReaccion, Long> conteo = reaccionPublicacionService.getConteoReaccionesByPublicacionId(idPublicacion);
        conteo.putIfAbsent(TipoReaccion.like, 0L);
        conteo.putIfAbsent(TipoReaccion.dislike, 0L);
        return new ResponseEntity<>(conteo, HttpStatus.OK);
    }

    @GetMapping("/usuario/{idUsuario}/publicacion/{idPublicacion}")
    public ResponseEntity<TipoReaccion> getReaccionUsuario(
            @PathVariable Long idUsuario,
            @PathVariable Long idPublicacion) {
        Optional<TipoReaccion> tipoReaccion = reaccionPublicacionService.getReaccionUsuarioAPublicacion(idUsuario, idPublicacion);
        return tipoReaccion.map(reaccion -> new ResponseEntity<>(reaccion, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}