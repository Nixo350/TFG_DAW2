package com.gestion.zarpas_backend.controlador;

import com.gestion.zarpas_backend.modelo.UsuarioRol;
import com.gestion.zarpas_backend.servicio.UsuarioRolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuario-roles")
public class UsuarioRolController {

    private final UsuarioRolService usuarioRolService;

    @Autowired
    public UsuarioRolController(UsuarioRolService usuarioRolService) {
        this.usuarioRolService = usuarioRolService;
    }

    @PostMapping
    public ResponseEntity<UsuarioRol> crearUsuarioRol(@RequestBody UsuarioRol usuarioRol) {
        // En un escenario real, deberías validar que el usuario y el rol existan
        UsuarioRol nuevoUsuarioRol = usuarioRolService.guardarUsuarioRol(usuarioRol);
        return new ResponseEntity<>(nuevoUsuarioRol, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioRol>> obtenerTodosLosUsuarioRoles() {
        List<UsuarioRol> usuarioRoles = usuarioRolService.obtenerTodosLosUsuarioRoles();
        return new ResponseEntity<>(usuarioRoles, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioRol> obtenerUsuarioRolPorId(@PathVariable("id") Long id) {
        return usuarioRolService.obtenerUsuarioRolPorId(id)
                .map(usuarioRol -> new ResponseEntity<>(usuarioRol, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuarioRol(@PathVariable("id") Long id) {
        try {
            usuarioRolService.eliminarUsuarioRol(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<UsuarioRol>> obtenerRolesPorUsuarioId(@PathVariable Long usuarioId) {
        List<UsuarioRol> usuarioRoles = usuarioRolService.obtenerRolesPorUsuarioId(usuarioId);
        return new ResponseEntity<>(usuarioRoles, HttpStatus.OK);
    }

    @GetMapping("/rol/{rolId}")
    public ResponseEntity<List<UsuarioRol>> obtenerUsuariosPorRolId(@PathVariable Long rolId) {
        List<UsuarioRol> usuarioRoles = usuarioRolService.obtenerUsuariosPorRolId(rolId);
        return new ResponseEntity<>(usuarioRoles, HttpStatus.OK);
    }
}