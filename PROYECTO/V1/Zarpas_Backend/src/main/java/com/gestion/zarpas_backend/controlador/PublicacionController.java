package com.gestion.zarpas_backend.controlador;

import com.gestion.zarpas_backend.modelo.*;
import com.gestion.zarpas_backend.servicio.PublicacionService;
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
import java.util.NoSuchElementException;
import java.util.Optional;
@RestController
@RequestMapping("/api/publicaciones")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class PublicacionController {

    private final PublicacionService publicacionService;
    private final UsuarioService usuarioService;
    private final StorageService storageService;

    @Autowired
    public PublicacionController(PublicacionService publicacionService, UsuarioService usuarioService, StorageService storageService) {
        this.publicacionService = publicacionService;
        this.usuarioService = usuarioService;
        this.storageService = storageService;
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
            publicacion.setFechaModificacion(Timestamp.from(Instant.now()));

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
                publicacion.setImagenUrl( fileName);
            }

            Publicacion nuevaPublicacion = publicacionService.guardarPublicacion(publicacion);
            return new ResponseEntity<>(nuevaPublicacion, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error al crear la publicación: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{idPublicacion}/save")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> savePublicacion(@PathVariable Long idPublicacion, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long idUsuario = userDetails.getId();

        publicacionService.guardarPublicacionPorUsuario(idPublicacion, idUsuario);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{idPublicacion}/unsave")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unsavePublicacion(@PathVariable Long idPublicacion, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long idUsuario = userDetails.getId();

        publicacionService.eliminarPublicacionGuardadaPorUsuario(idPublicacion, idUsuario);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/todas")
    public ResponseEntity<List<Publicacion>> obtenerTodasLasPublicaciones() {
        List<Publicacion> publicaciones = publicacionService.obtenerTodasLasPublicaciones();
        return new ResponseEntity<>(publicaciones, HttpStatus.OK);
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

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<Publicacion>> getPublicacionesByUserId(@PathVariable Long userId) {
        List<Publicacion> publicaciones = publicacionService.getPublicacionesByUserId(userId);
        return ResponseEntity.ok(publicaciones);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @publicacionServiceImpl.isPublicacionOwner(#id, authentication.principal.id)")
    public ResponseEntity<Void> deletePublicacion(@PathVariable Long id) {
        try {
            publicacionService.deletePublicacion(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }





}