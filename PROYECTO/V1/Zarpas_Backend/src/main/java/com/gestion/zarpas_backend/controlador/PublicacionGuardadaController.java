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
@RequestMapping("/api/publicaciones")
@CrossOrigin(origins = "http://localhost:4200")
public class PublicacionGuardadaController {

    private final PublicacionGuardadaService publicacionGuardadaService;

    @Autowired
    public PublicacionGuardadaController(PublicacionGuardadaService publicacionGuardadaService) {
        this.publicacionGuardadaService = publicacionGuardadaService;
    }


    @GetMapping("/usuario/{idUsuario}/guardadas")
    public ResponseEntity<List<PublicacionGuardada>> getPublicacionesGuardadasByUsuario(@PathVariable Long idUsuario) {
        List<PublicacionGuardada> publicacionesGuardadas = publicacionGuardadaService.getPublicacionesGuardadasByUsuario(idUsuario);
        return ResponseEntity.ok(publicacionesGuardadas);
    }


}