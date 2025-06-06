package com.gestion.zarpas_backend.controlador;

import com.gestion.zarpas_backend.modelo.Publicacion;
import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.servicio.PublicacionService;
import com.gestion.zarpas_backend.servicio.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Mantén si lo usas en otros métodos, si no, puedes quitarlo
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.gestion.zarpas_backend.servicio.StorageService;

import java.sql.Timestamp;
import java.util.List;

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

    @PostMapping
    public ResponseEntity<?> crearPublicacion(
            // @RequestPart es para recibir partes individuales de un FormData
            @RequestPart("titulo") String titulo,
            @RequestPart("contenido") String contenido,
            @RequestPart("idUsuario") String idUsuarioString, // El ID del usuario se envía como String
            @RequestPart(value = "file", required = false) MultipartFile file // El archivo de imagen es opcional
    ) {
        try {
            // Convertir el idUsuario de String a Long
            Long idUsuario = Long.parseLong(idUsuarioString);

            // Buscar el usuario por su ID
            Usuario usuario = usuarioService.obtenerUsuarioPorId(idUsuario)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));

            // Crear la nueva publicación
            Publicacion publicacion = new Publicacion();
            publicacion.setTitulo(titulo);
            publicacion.setContenido(contenido);
            publicacion.setUsuario(usuario);
            publicacion.setFechaCreacion(new Timestamp(System.currentTimeMillis()));

            String imageUrl = null;
            // Si se proporcionó un archivo, almacenarlo usando StorageService
            if (file != null && !file.isEmpty()) {
                imageUrl = storageService.store(file); // Almacena el archivo en el disco
                publicacion.setImagenUrl(imageUrl); // Guarda la URL relativa en la entidad Publicacion
            }

            // Guardar la publicación en la base de datos
            Publicacion nuevaPublicacion = publicacionService.guardarPublicacion(publicacion);
            return new ResponseEntity<>(nuevaPublicacion, HttpStatus.CREATED);

        } catch (NumberFormatException e) {
            // Error si el idUsuario no es un número válido
            return new ResponseEntity<>("Error de formato en idUsuario: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            // Errores específicos como usuario no encontrado o problemas de almacenamiento
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // O HttpStatus.BAD_REQUEST si es un problema de entrada
        } catch (Exception e) {
            // Captura cualquier otra excepción inesperada
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
}