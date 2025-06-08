package com.gestion.zarpas_backend.controlador;

import com.gestion.zarpas_backend.modelo.PublicacionGuardada;
import com.gestion.zarpas_backend.modelo.PublicacionGuardadaId;
import com.gestion.zarpas_backend.servicio.PublicacionGuardadaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publicaciones") // <-- ¡CAMBIO REALIZADO AQUÍ!
@CrossOrigin(origins = "http://localhost:4200") // Añade esta anotación si no la tienes para permitir peticiones desde Angular
public class PublicacionGuardadaController {

    private final PublicacionGuardadaService publicacionGuardadaService;

    @Autowired
    public PublicacionGuardadaController(PublicacionGuardadaService publicacionGuardadaService) {
        this.publicacionGuardadaService = publicacionGuardadaService;
    }

    // Endpoint para guardar una publicación para un usuario (más amigable para el cliente)
    @PostMapping("/usuario/{idUsuario}/publicacion/{idPublicacion}")
    public ResponseEntity<PublicacionGuardada> guardarPublicacionParaUsuario(
            @PathVariable Long idUsuario, @PathVariable Long idPublicacion) {
        try {
            PublicacionGuardada pg = publicacionGuardadaService.guardarPublicacionParaUsuario(idUsuario, idPublicacion);
            return new ResponseEntity<>(pg, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Log the exception for debugging
            System.err.println("Error al guardar publicación para usuario: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Por ejemplo, si ya existe o no encuentra usuario/publicación
        } catch (Exception e) {
            System.err.println("Error interno del servidor al guardar publicación: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/usuario/{idUsuario}/guardadas") // Este path ahora sí coincidirá con el frontend: /api/publicaciones/usuario/{idUsuario}/guardadas
    public ResponseEntity<List<PublicacionGuardada>> getPublicacionesGuardadasByUsuario(@PathVariable Long idUsuario) {
        List<PublicacionGuardada> publicacionesGuardadas = publicacionGuardadaService.getPublicacionesGuardadasByUsuario(idUsuario);
        return ResponseEntity.ok(publicacionesGuardadas);
    }

    @GetMapping
    public ResponseEntity<List<PublicacionGuardada>> obtenerTodasLasPublicacionesGuardadas() {
        List<PublicacionGuardada> pgs = publicacionGuardadaService.obtenerTodasLasPublicacionesGuardadas();
        return new ResponseEntity<>(pgs, HttpStatus.OK);
    }



    // Para eliminar, se necesitan ambos IDs
    @DeleteMapping("/usuario/{idUsuario}/publicacion/{idPublicacion}") // Esto también se mapea a /api/publicaciones/usuario/{idUsuario}/publicacion/{idPublicacion}
    public ResponseEntity<Void> eliminarPublicacionGuardada(
            @PathVariable Long idUsuario, @PathVariable Long idPublicacion) {
        try {
            publicacionGuardadaService.eliminarPublicacionGuardadaParaUsuario(idUsuario, idPublicacion);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            System.err.println("Error al eliminar publicación guardada: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Opcional: Si necesitaras un endpoint para obtener por el ID compuesto (menos común desde el frontend)
    // Este endpoint recibiría los IDs como parámetros de la ruta, no como un objeto JSON
    @GetMapping("/{idUsuario}/{idPublicacion}") // Esto también se mapea a /api/publicaciones/{idUsuario}/{idPublicacion}
    public ResponseEntity<PublicacionGuardada> obtenerPublicacionGuardadaPorIds(
            @PathVariable Long idUsuario, @PathVariable Long idPublicacion) {
        PublicacionGuardadaId id = new PublicacionGuardadaId();
        id.setIdUsuario(idUsuario);
        id.setIdPublicacion(idPublicacion);
        return publicacionGuardadaService.obtenerPublicacionGuardadaPorId(id)
                .map(pg -> new ResponseEntity<>(pg, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}