// src/main/java/com/gestion/zarpas_backend/controlador/CategoriaController.java
package com.gestion.zarpas_backend.controlador;

import com.gestion.zarpas_backend.modelo.Categoria;
import com.gestion.zarpas_backend.servicio.CategoriaService; // Necesitarás este servicio
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categorias") // Ruta base específica para categorías
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class CategoriaController {

    private final CategoriaService categoriaService; // Inyecta el nuevo servicio

    @Autowired
    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    // DTO simple para crear/actualizar categoría (solo nombre)
    public static class CategoriaRequest {
        public String nombre;
        public String descripcion; // Opcional, si quieres permitir crearla también
    }

    @PostMapping // POST /api/categorias (crear una nueva categoría)
    public ResponseEntity<?> crearCategoria(@RequestBody CategoriaRequest request) {
        try {
            // Verifica si ya existe una categoría con ese nombre (opcional pero buena práctica)
            Optional<Categoria> existingCategory = categoriaService.obtenerCategoriaPorNombre(request.nombre);
            if (existingCategory.isPresent()) {
                return new ResponseEntity<>("La categoría con el nombre '" + request.nombre + "' ya existe.", HttpStatus.CONFLICT);
            }

            Categoria nuevaCategoria = new Categoria();
            nuevaCategoria.setNombre(request.nombre);
            nuevaCategoria.setDescripcion(request.descripcion); // Puedes setear la descripción también

            Categoria categoriaGuardada = categoriaService.guardarCategoria(nuevaCategoria);
            return new ResponseEntity<>(categoriaGuardada, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear la categoría: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all") // GET /api/categorias/all (obtener todas las categorías)
    public ResponseEntity<List<Categoria>> obtenerTodasLasCategorias() {
        List<Categoria> categorias = categoriaService.obtenerTodasLasCategorias();
        return new ResponseEntity<>(categorias, HttpStatus.OK);
    }

    // Otros endpoints (obtener por ID, actualizar, eliminar) si los necesitas
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> obtenerCategoriaPorId(@PathVariable Long id) {
        return categoriaService.obtenerCategoriaPorId(id)
                .map(categoria -> new ResponseEntity<>(categoria, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCategoria(@PathVariable Long id, @RequestBody CategoriaRequest request) {
        try {
            Categoria categoriaActualizada = categoriaService.actualizarCategoria(id, request.nombre, request.descripcion);
            return new ResponseEntity<>(categoriaActualizada, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Long id) {
        try {
            categoriaService.eliminarCategoria(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}