package com.gestion.zarpas_backend.controlador;

import com.gestion.zarpas_backend.dto.ContrasenaChangeRequest;
import com.gestion.zarpas_backend.dto.UsuarioDTO;
import com.gestion.zarpas_backend.dto.UsuarioPerfilUpdateRequest;
import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.modelo.Rol;
import com.gestion.zarpas_backend.servicio.StorageService;
import com.gestion.zarpas_backend.servicio.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

    private final UsuarioService usuarioService;
    @Autowired
    private StorageService storageService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<Usuario>> obtenerTodosLosUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUsuarioById(@PathVariable Long id) {

        Optional<Usuario> usuarioOptional = usuarioService.obtenerUsuarioPorId(id);

        if (usuarioOptional.isPresent()) {
            return ResponseEntity.ok(usuarioOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable("id") Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{usuarioId}/roles/{rolId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> agregarRolAUsuario(@PathVariable Long usuarioId, @PathVariable Long rolId) {
        try {
            Usuario usuarioConRol = usuarioService.agregarRolAUsuario(usuarioId, rolId);
            return new ResponseEntity<>(usuarioConRol, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{usuarioId}/roles")
    @PreAuthorize("hasRole('ADMIN') or #usuarioId == authentication.principal.id")
    public ResponseEntity<Set<Rol>> obtenerRolesDeUsuario(@PathVariable Long usuarioId) {
        try {
            Set<Rol> roles = usuarioService.obtenerRolesDeUsuario(usuarioId);
            return new ResponseEntity<>(roles, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/perfil")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<?> updateUsuarioPerfil(@PathVariable Long id, @Valid @RequestBody UsuarioPerfilUpdateRequest usuarioPerfilUpdateRequest) {
        try {
            Usuario updatedUser = usuarioService.updateUsuarioPerfil(id, usuarioPerfilUpdateRequest);
            return ResponseEntity.ok(updatedUser);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/perfil/imagen")
    public ResponseEntity<?> updateUserProfileWithImage(
            @PathVariable Long id,
            @RequestParam("username") String username,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {


            // Ejemplo:
            Usuario usuario = usuarioService.obtenerUsuarioPorId(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            usuario.setUsername(username);

            if (file != null && !file.isEmpty()) {
                String imageUrl = storageService.store(file);
                usuario.setFotoPerfil(imageUrl);
            }

            Usuario usuarioActualizado = usuarioService.guardarUsuario(usuario);
            return ResponseEntity.ok(new UsuarioDTO(usuarioActualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body("Error al subir la imagen: " + e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
        }
    }


    @PutMapping("/{id}/cambiar-contrasena")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @Valid @RequestBody ContrasenaChangeRequest contrasenaChangeRequest) {
        try {
            usuarioService.changePassword(id, contrasenaChangeRequest.getNewContrasena());
            return ResponseEntity.ok("Contraseña actualizada con éxito!");
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor: " + e.getMessage());
        }
    }

}