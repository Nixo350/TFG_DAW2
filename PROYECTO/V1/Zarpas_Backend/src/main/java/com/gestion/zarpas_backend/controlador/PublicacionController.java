package com.gestion.zarpas_backend.controlador;

import com.gestion.zarpas_backend.modelo.Publicacion;
import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.servicio.PublicacionService;
import com.gestion.zarpas_backend.servicio.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Mantén si lo usas en otros métodos, si no, puedes quitarlo
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.gestion.zarpas_backend.servicio.StorageService;
import com.gestion.zarpas_backend.modelo.Categoria; // Importa Categoria

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/publicaciones")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class PublicacionController {

    private final PublicacionService publicacionService;
    private final UsuarioService usuarioService;
    @Autowired
    private StorageService storageService;

    @Autowired
    public PublicacionController(PublicacionService publicacionService, UsuarioService usuarioService) {
        this.publicacionService = publicacionService;
        this.usuarioService = usuarioService;
    }

    // CAMBIO CRUCIAL AQUÍ
    @PostMapping("/crear-con-imagen") // Cambié la ruta para que sea más explícita, puedes usar @PostMapping si es la única POST
    public ResponseEntity<?> crearPublicacion(
            @RequestPart("titulo") String titulo,
            @RequestPart("contenido") String contenido,
            // @RequestPart("idUsuario") String idUsuarioString, // ¡ELIMINA ESTA LÍNEA!
            @RequestPart(value = "imagen", required = false) MultipartFile imagen, // Cambié 'file' a 'imagen' para que coincida con Angular
            @RequestPart(value = "createNewCategory", required = false) String createNewCategoryString,
            @RequestPart(value = "newCategoryName", required = false) String newCategoryName,
            @RequestPart(value = "newCategoryDescription", required = false) String newCategoryDescription,
            @RequestPart(value = "selectedCategoryId", required = false) String selectedCategoryIdString // Recibir como String
    ) {
        try {
            // 1. Obtener el ID del usuario autenticado de Spring Security
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado.");
            }
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();

            Optional<Usuario> usuarioOptional = usuarioService.findByUsername(username); // Asegúrate de tener findByUsername en UsuarioService
            if (!usuarioOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario autenticado no encontrado en la base de datos.");
            }
            Usuario usuario = usuarioOptional.get(); // Obtener el objeto Usuario completo

            // Procesar la categoría
            Boolean createNewCategory = Boolean.parseBoolean(createNewCategoryString);
            Long selectedCategoryId = null;
            if (selectedCategoryIdString != null && !selectedCategoryIdString.isEmpty() && !createNewCategory) {
                selectedCategoryId = Long.parseLong(selectedCategoryIdString);
            }

            String imageUrl = null;
            if (imagen != null && !imagen.isEmpty()) { // Usar 'imagen' en lugar de 'file'
                imageUrl = storageService.store(imagen);
            }

            // Crear la nueva publicación
            Publicacion publicacion = new Publicacion();
            publicacion.setTitulo(titulo);
            publicacion.setContenido(contenido);
            publicacion.setUsuario(usuario); // Establecer el objeto Usuario
            publicacion.setFechaCreacion(new Timestamp(System.currentTimeMillis()));
            publicacion.setImagenUrl(imageUrl);

            // Manejar la asignación/creación de categoría
            if (createNewCategory) {
                if (newCategoryName == null || newCategoryName.trim().isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El nombre de la nueva categoría no puede estar vacío.");
                }
                Categoria newCategory = new Categoria();
                newCategory.setNombre(newCategoryName);
                newCategory.setDescripcion(newCategoryDescription);
                // Asegúrate de que tu PublicacionService.crearCategoria existe y maneja la lógica
                Categoria createdCategory = publicacionService.crearCategoria(newCategory);
                publicacion.setCategoria(createdCategory);
            } else if (selectedCategoryId != null) {
                publicacion.setCategoria(publicacionService.getCategoriaById(selectedCategoryId) // Asegúrate de tener este método en PublicacionService
                        .orElseThrow(() -> new RuntimeException("Categoría seleccionada no encontrada")));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Debe seleccionar una categoría o crear una nueva.");
            }

            // Guardar la publicación en la base de datos
            Publicacion nuevaPublicacion = publicacionService.guardarPublicacion(publicacion);
            return new ResponseEntity<>(nuevaPublicacion, HttpStatus.CREATED);

        } catch (NumberFormatException e) {
            return new ResponseEntity<>("Error de formato en ID de categoría: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("Error interno del servidor al crear la publicación: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>("Error interno del servidor al crear la publicación: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping({"/todas", "/todas/"})
    public ResponseEntity<List<Publicacion>> obtenerTodasLasPublicaciones() {
        System.out.println("PublicacionController: Entrando en obtenerTodasLasPublicaciones()..."); // Puedes quitar este System.out.println si quieres
        try {
            List<Publicacion> publicaciones = publicacionService.obtenerTodasLasPublicaciones();
            System.out.println("PublicacionController: publicacionService.obtenerTodasLasPublicaciones() completado. Número de publicaciones: " + publicaciones.size()); // Puedes quitar este System.out.println si quieres
            return new ResponseEntity<>(publicaciones, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("PublicacionController: ¡ERROR al obtener todas las publicaciones! " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Publicacion> obtenerPublicacionPorId(@PathVariable Long id) {
        return publicacionService.obtenerPublicacionPorId(id)
                .map(publicacion -> new ResponseEntity<>(publicacion, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Publicacion>> obtenerPublicacionesPorUsuario(@PathVariable Long usuarioId) {
        return usuarioService.obtenerUsuarioPorId(usuarioId)
                .map(usuario -> {
                    List<Publicacion> publicaciones = publicacionService.obtenerPublicacionesPorUsuario(usuario);
                    return new ResponseEntity<>(publicaciones, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Publicacion> actualizarPublicacion(@PathVariable("id") Long id, @RequestBody Publicacion publicacion) {
        if (!id.equals(publicacion.getIdPublicacion())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            Publicacion publicacionActualizada = publicacionService.actualizarPublicacion(publicacion);
            return new ResponseEntity<>(publicacionActualizada, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPublicacion(@PathVariable("id") Long id) {
        try {
            publicacionService.eliminarPublicacion(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Publicacion>> searchPublicaciones(@RequestParam String query) {
        List<Publicacion> results = publicacionService.searchPublicaciones(query);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/categoria/{nombreCategoria}")
    public ResponseEntity<List<Publicacion>> getPublicacionesByCategoria(@PathVariable String nombreCategoria) { // Devuelve List<Publicacion>
        List<Publicacion> publicaciones = publicacionService.getPublicacionesByCategoria(nombreCategoria);
        return ResponseEntity.ok(publicaciones);
    }

    @GetMapping("/categorias/all")
    public ResponseEntity<List<Categoria>> getAllCategorias() { // Cambiado a List<Categoria>
        List<Categoria> categorias = publicacionService.getAllCategorias(); // publicacionService ya devuelve List<Categoria>
        return ResponseEntity.ok(categorias);
    }

    @PostMapping("/categorias") // Ruta para crear una nueva categoría
    @PreAuthorize("isAuthenticated()") // Requiere que el usuario esté logueado para crear una categoría
    public ResponseEntity<Categoria> crearCategoria(@RequestBody Categoria categoria) {
        try {
            // Puedes añadir validaciones adicionales aquí, por ejemplo, que el nombre no sea nulo/vacío
            if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // O un mensaje de error más específico
            }

            //Opcional: Si quieres evitar duplicados y lanzar un error si ya existe
             if (publicacionService.getAllCategorias().stream().anyMatch(c -> c.getNombre().equalsIgnoreCase(categoria.getNombre()))) {
                 return new ResponseEntity<>(HttpStatus.CONFLICT); // 409 Conflict
             }

            Categoria nuevaCategoria = publicacionService.crearCategoria(categoria);
            return new ResponseEntity<>(nuevaCategoria, HttpStatus.CREATED);
        } catch (Exception e) {
            // Log the exception for debugging
            System.err.println("Error al crear la categoría: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}