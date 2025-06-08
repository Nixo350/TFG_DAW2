// src/main/java/com/gestion/zarpas_backend/controlador/PublicacionController.java
package com.gestion.zarpas_backend.controlador;

import com.gestion.zarpas_backend.modelo.*;
import com.gestion.zarpas_backend.servicio.PublicacionService;
import com.gestion.zarpas_backend.servicio.ReaccionPublicacionService;
import com.gestion.zarpas_backend.servicio.UsuarioService;
import com.gestion.zarpas_backend.servicio.impl.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.gestion.zarpas_backend.servicio.StorageService;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/publicaciones")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class PublicacionController {

    private final PublicacionService publicacionService;
    private final UsuarioService usuarioService;
    private final StorageService storageService; // Asegúrate de que este servicio está inyectado si manejas imágenes
    @Autowired
    private ReaccionPublicacionService reaccionPublicacionService;

    @Autowired
    public PublicacionController(PublicacionService publicacionService, UsuarioService usuarioService, StorageService storageService) {
        this.publicacionService = publicacionService;
        this.usuarioService = usuarioService;
        this.storageService = storageService; // Inyecta el StorageService
    }

    // Método auxiliar para obtener el usuario autenticado
    private Optional<Usuario> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return usuarioService.findByUsername(userDetails.getUsername());
    }


    @PostMapping("/crear-con-imagen")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> crearPublicacionConImagen(
            @RequestParam("titulo") String titulo,
            @RequestParam("contenido") String contenido,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            @RequestParam(value = "categoriaNombre") String categoriaNombre) {
        try {
            Optional<Usuario> currentUser = getAuthenticatedUser();
            if (!currentUser.isPresent()) {
                return new ResponseEntity<>("Usuario no autenticado", HttpStatus.UNAUTHORIZED);
            }

            Publicacion publicacion = new Publicacion();
            publicacion.setTitulo(titulo);
            publicacion.setContenido(contenido);
            publicacion.setUsuario(currentUser.get());
            publicacion.setFechaCreacion(Timestamp.from(Instant.now()));
            publicacion.setFechaModificacion(Timestamp.from(Instant.now())); // Establecer también al crear

            // Manejo de la categoría
            if (categoriaNombre != null && !categoriaNombre.trim().isEmpty()) {
                Optional<Categoria> categoriaExistente = publicacionService.getAllCategorias().stream()
                        .filter(c -> c.getNombre().equalsIgnoreCase(categoriaNombre))
                        .findFirst();
                if (categoriaExistente.isPresent()) {
                    publicacion.setCategoria(categoriaExistente.get());
                } else {
                    Categoria nuevaCategoria = new Categoria();
                    nuevaCategoria.setNombre(categoriaNombre);
                    publicacion.setCategoria(publicacionService.crearCategoria(nuevaCategoria));
                }
            }

            if (imagen != null && !imagen.isEmpty()) {
                String fileName = storageService.store(imagen);
                publicacion.setImagenUrl( fileName); // Guardar la ruta relativa o absoluta si tu frontend la necesita así
            }

            Publicacion nuevaPublicacion = publicacionService.guardarPublicacion(publicacion);
            return new ResponseEntity<>(nuevaPublicacion, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error al crear la publicación: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{idPublicacion}/save")
    @PreAuthorize("isAuthenticated()") // Requiere autenticación
    public ResponseEntity<Void> savePublicacion(@PathVariable Long idPublicacion, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long idUsuario = userDetails.getId(); // Asumiendo que UserDetailsImpl tiene un getId()

        publicacionService.guardarPublicacionPorUsuario(idPublicacion, idUsuario);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{idPublicacion}/unsave")
    @PreAuthorize("isAuthenticated()") // Requiere autenticación
    public ResponseEntity<Void> unsavePublicacion(@PathVariable Long idPublicacion, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long idUsuario = userDetails.getId(); // Asumiendo que UserDetailsImpl tiene un getId()

        publicacionService.eliminarPublicacionGuardadaPorUsuario(idPublicacion, idUsuario);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/todas")
    public ResponseEntity<List<Publicacion>> obtenerTodasLasPublicaciones() {
        List<Publicacion> publicaciones = publicacionService.obtenerTodasLasPublicaciones();
        return new ResponseEntity<>(publicaciones, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Publicacion> obtenerPublicacionPorId(@PathVariable Long id) {
        Optional<Publicacion> publicacion = publicacionService.obtenerPublicacionPorId(id);
        return publicacion.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Publicacion>> obtenerPublicacionesPorUsuario(@PathVariable Long idUsuario) {
        Optional<Usuario> usuario = usuarioService.obtenerUsuarioPorId(idUsuario);
        return usuario.map(value -> {
            List<Publicacion> publicaciones = publicacionService.obtenerPublicacionesPorUsuario(value);
            return new ResponseEntity<>(publicaciones, HttpStatus.OK);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Publicacion>> searchPublicaciones(@RequestParam String keyword) {
        List<Publicacion> publicaciones = publicacionService.searchPublicaciones(keyword);
        return new ResponseEntity<>(publicaciones, HttpStatus.OK);
    }

    @GetMapping("/categoria/{nombreCategoria}")
    public ResponseEntity<List<Publicacion>> getPublicacionesByCategoria(@PathVariable String nombreCategoria) {
        List<Publicacion> publicaciones = publicacionService.getPublicacionesByCategoria(nombreCategoria);
        if (publicaciones.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(publicaciones, HttpStatus.OK);
    }

    @GetMapping("/categorias/all")
    public ResponseEntity<List<Categoria>> getAllCategorias() {
        List<Categoria> categorias = publicacionService.getAllCategorias();
        return ResponseEntity.ok(categorias);
    }

    @PostMapping("/categorias")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Categoria> crearCategoria(@RequestBody Categoria categoria) {
        try {
            if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (publicacionService.getAllCategorias().stream().anyMatch(c -> c.getNombre().equalsIgnoreCase(categoria.getNombre()))) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            Categoria nuevaCategoria = publicacionService.crearCategoria(categoria);
            return new ResponseEntity<>(nuevaCategoria, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("Error al crear la categoría: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // --- ¡NUEVO: Endpoint para ACTUALIZAR una publicación! ---
    @PutMapping("/{idPublicacion}")
    @PreAuthorize("isAuthenticated()") // Requiere que el usuario esté logueado
    public ResponseEntity<?> actualizarPublicacion(
            @PathVariable Long idPublicacion,
            @RequestBody Publicacion publicacionActualizada) {
        try {
            Optional<Usuario> currentUserOptional = getAuthenticatedUser();
            if (!currentUserOptional.isPresent()) {
                return new ResponseEntity<>("Usuario no autenticado", HttpStatus.UNAUTHORIZED);
            }
            Usuario currentUser = currentUserOptional.get();

            Publicacion publicacionExistente = publicacionService.obtenerPublicacionPorId(idPublicacion)
                    .orElseThrow(() -> new RuntimeException("Publicación no encontrada con ID: " + idPublicacion));

            // Verificar si el usuario autenticado es el propietario de la publicación o un ADMIN
            boolean isOwner = publicacionExistente.getUsuario().getIdUsuario().equals(currentUser.getIdUsuario());
            boolean isAdmin = currentUser.getUsuarioRoles().stream()
                    .anyMatch(ur -> ur.getRol().getNombre().equals("ADMIN"));

            if (!isOwner && !isAdmin) {
                return new ResponseEntity<>("No tienes permiso para actualizar esta publicación.", HttpStatus.FORBIDDEN);
            }

            // Asegurarse de que el ID de la publicación en el cuerpo coincide con el ID de la URL
            if (!publicacionActualizada.getIdPublicacion().equals(idPublicacion)) {
                return new ResponseEntity<>("El ID de la publicación en el cuerpo no coincide con el ID de la URL.", HttpStatus.BAD_REQUEST);
            }

            // La lógica de actualización de campos y fechaModificacion ya está en el servicio
            Publicacion publicacionGuardada = publicacionService.actualizarPublicacion(publicacionActualizada);
            return new ResponseEntity<>(publicacionGuardada, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // Publicación no encontrada, etc.
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error interno del servidor al actualizar la publicación: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // --- ¡NUEVO: Endpoint para ELIMINAR una publicación! ---
    @DeleteMapping("/{idPublicacion}")
    @PreAuthorize("isAuthenticated()") // Requiere que el usuario esté logueado
    public ResponseEntity<?> eliminarPublicacion(@PathVariable Long idPublicacion) {
        try {
            Optional<Usuario> currentUserOptional = getAuthenticatedUser();
            if (!currentUserOptional.isPresent()) {
                return new ResponseEntity<>("Usuario no autenticado", HttpStatus.UNAUTHORIZED);
            }
            Usuario currentUser = currentUserOptional.get();

            Publicacion publicacionExistente = publicacionService.obtenerPublicacionPorId(idPublicacion)
                    .orElseThrow(() -> new RuntimeException("Publicación no encontrada con ID: " + idPublicacion));

            // Verificar si el usuario autenticado es el propietario de la publicación o un ADMIN
            boolean isOwner = publicacionExistente.getUsuario().getIdUsuario().equals(currentUser.getIdUsuario());
            boolean isAdmin = currentUser.getUsuarioRoles().stream()
                    .anyMatch(ur -> ur.getRol().getNombre().equals("ADMIN"));

            if (!isOwner && !isAdmin) {
                return new ResponseEntity<>("No tienes permiso para eliminar esta publicación.", HttpStatus.FORBIDDEN);
            }

            publicacionService.eliminarPublicacion(idPublicacion);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // Publicación no encontrada
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error interno del servidor al eliminar la publicación: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/publicacion/{idPublicacion}/usuario/{idUsuario}")
    public ResponseEntity<TipoReaccion> getReaccionByPublicacionAndUsuario(
            @PathVariable Long idPublicacion,
            @PathVariable Long idUsuario) {
        Optional<ReaccionPublicacion> reaccion = reaccionPublicacionService.findByPublicacionIdAndUsuarioId(idPublicacion, idUsuario);
        if (reaccion.isPresent()) {
            return ResponseEntity.ok(reaccion.get().getTipoReaccion());
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content si no hay reacción
        }
    }
}