package com.gestion.zarpas_backend.controlador;

import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.modelo.Rol;
import com.gestion.zarpas_backend.servicio.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario) {
        Usuario nuevoUsuario = usuarioService.guardarUsuario(usuario);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerTodosLosUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable("id") Long id) {
        return usuarioService.obtenerUsuarioPorId(id)
                .map(usuario -> new ResponseEntity<>(usuario, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> obtenerUsuarioPorEmail(@PathVariable("email") String email) {
        return usuarioService.obtenerUsuarioPorEmail(email)
                .map(usuario -> new ResponseEntity<>(usuario, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable("id") Long id, @RequestBody Usuario usuario) {
        if (!id.equals(usuario.getIdUsuario())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // ID en path no coincide con el del cuerpo
        }
        try {
            Usuario usuarioActualizado = usuarioService.actualizarUsuario(usuario);
            return new ResponseEntity<>(usuarioActualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable("id") Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{usuarioId}/roles/{rolId}")
    public ResponseEntity<Usuario> agregarRolAUsuario(@PathVariable Long usuarioId, @PathVariable Long rolId) {
        try {
            Usuario usuarioConRol = usuarioService.agregarRolAUsuario(usuarioId, rolId);
            return new ResponseEntity<>(usuarioConRol, HttpStatus.OK);
        } catch (RuntimeException e) {
            // Manejar diferentes excepciones si es necesario, por ejemplo, si el rol ya está asignado
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{usuarioId}/roles")
    public ResponseEntity<Set<Rol>> obtenerRolesDeUsuario(@PathVariable Long usuarioId) {
        try {
            Set<Rol> roles = usuarioService.obtenerRolesDeUsuario(usuarioId);
            return new ResponseEntity<>(roles, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}