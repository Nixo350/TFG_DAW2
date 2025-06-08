// src/main/java/com/gestion/zarpas_backend/servicio/impl/PublicacionServiceImpl.java
package com.gestion.zarpas_backend.servicio.impl;

import com.gestion.zarpas_backend.modelo.Categoria;
import com.gestion.zarpas_backend.modelo.Publicacion;
import com.gestion.zarpas_backend.modelo.PublicacionGuardada;
import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.repositorio.CategoriaRepository;
import com.gestion.zarpas_backend.repositorio.PublicacionGuardadaRepository;
import com.gestion.zarpas_backend.repositorio.PublicacionRepository;
import com.gestion.zarpas_backend.repositorio.UsuarioRepository;
import com.gestion.zarpas_backend.servicio.PublicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class PublicacionServiceImpl implements PublicacionService {

    private final PublicacionRepository publicacionRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    @Autowired
    private PublicacionGuardadaRepository publicacionGuardadaRepository;

    @Autowired
    public PublicacionServiceImpl(PublicacionRepository publicacionRepository,
                                  UsuarioRepository usuarioRepository,
                                  CategoriaRepository categoriaRepository) {
        this.publicacionRepository = publicacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional
    public Publicacion guardarPublicacion(Publicacion publicacion) {
        // Asignar fecha de creación si es nueva
        if (publicacion.getFechaCreacion() == null) {
            publicacion.setFechaCreacion(Timestamp.from(Instant.now()));
        }
        // Asegurarse de que la fecha de modificación también se establezca al crear
        publicacion.setFechaModificacion(Timestamp.from(Instant.now()));

        // Manejo de la categoría por nombre (si se envía)
        if (publicacion.getCategoria() != null && publicacion.getCategoria().getNombre() != null) {
            String nombreCategoria = publicacion.getCategoria().getNombre();
            Optional<Categoria> existingCategory = categoriaRepository.findByNombreIgnoreCase(nombreCategoria);
            if (existingCategory.isPresent()) {
                publicacion.setCategoria(existingCategory.get());
            } else {
                // Si no existe, puedes crearla o lanzar un error. Aquí la creamos.
                Categoria nuevaCategoria = new Categoria();
                nuevaCategoria.setNombre(nombreCategoria);
                publicacion.setCategoria(categoriaRepository.save(nuevaCategoria));
            }
        }
        return publicacionRepository.save(publicacion);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Publicacion> obtenerPublicacionPorId(Long id) {
        return publicacionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Publicacion> obtenerTodasLasPublicaciones() {
        return publicacionRepository.findAllByOrderByFechaCreacionDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Publicacion> obtenerPublicacionesPorUsuario(Usuario usuario) {
        return publicacionRepository.findByUsuario(usuario);
    }

    @Override
    @Transactional
    public Publicacion actualizarPublicacion(Publicacion publicacionActualizada) {
        // 1. Buscar la publicación existente por ID
        Publicacion publicacionExistente = publicacionRepository.findById(publicacionActualizada.getIdPublicacion())
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada con ID: " + publicacionActualizada.getIdPublicacion()));

        // 2. Actualizar los campos que pueden ser modificados
        publicacionExistente.setTitulo(publicacionActualizada.getTitulo());
        publicacionExistente.setContenido(publicacionActualizada.getContenido());
        publicacionExistente.setImagenUrl(publicacionActualizada.getImagenUrl());
        // Actualizar la categoría si se proporciona una nueva
        if (publicacionActualizada.getCategoria() != null && publicacionActualizada.getCategoria().getNombre() != null) {
            String nombreCategoria = publicacionActualizada.getCategoria().getNombre();
            Optional<Categoria> existingCategory = categoriaRepository.findByNombreIgnoreCase(nombreCategoria);
            if (existingCategory.isPresent()) {
                publicacionExistente.setCategoria(existingCategory.get());
            } else {
                // Si no existe la categoría, se crea o se maneja el error.
                // Aquí, la crearemos si no existe.
                Categoria nuevaCategoria = new Categoria();
                nuevaCategoria.setNombre(nombreCategoria);
                publicacionExistente.setCategoria(categoriaRepository.save(nuevaCategoria));
            }
        } else {
            // Si la categoría se envía como null o vacía, podrías querer setearla a null o mantener la existente.
            // En este caso, la mantenemos si no se envía.
            // publicacionExistente.setCategoria(null); // Descomenta si quieres permitir quitar la categoría.
        }


        // 3. Actualizar la fecha de modificación
        publicacionExistente.setFechaModificacion(Timestamp.from(Instant.now()));

        // 4. Guardar la publicación actualizada
        return publicacionRepository.save(publicacionExistente);
    }

    @Override
    @Transactional
    public void eliminarPublicacion(Long id) {
        publicacionRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<Publicacion> searchPublicaciones(String keyword) {
        return publicacionRepository.searchByKeyword(keyword);
    }

    // --- ¡NUEVO: Método para obtener publicaciones por categoría! ---
    @Override
    @Transactional(readOnly = true)
    public List<Publicacion> getPublicacionesByCategoria(String nombreCategoria) {
        // Usa el nuevo método del repositorio que busca por el nombre de la CATEGORIA
        return publicacionRepository.findByCategoria_NombreIgnoreCaseOrderByFechaCreacionDesc(nombreCategoria);
    }

    // Nuevo método para obtener todas las categorías
    @Override
    public List<Categoria> getAllCategorias() {
        return categoriaRepository.findAll();
    }
    @Override
    @Transactional
    public Categoria crearCategoria(Categoria categoria) {
        // Opcional: Comprobar si ya existe una categoría con ese nombre
        // Optional<Categoria> existingCategory = categoriaRepository.findByNombre(categoria.getNombre());
        // if (existingCategory.isPresent()) {
        //     throw new RuntimeException("La categoría con el nombre '" + categoria.getNombre() + "' ya existe.");
        // }
        return categoriaRepository.save(categoria);
    }
    @Override
    public Optional<Categoria> getCategoriaById(Long id) {
        return categoriaRepository.findById(id); // <--- AÑADE ESTE MÉTODO
    }

    @Override
    @Transactional
    public void guardarPublicacionPorUsuario(Long idPublicacion, Long idUsuario) {
        Publicacion publicacion = publicacionRepository.findById(idPublicacion)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Prevenir duplicados (usando el repositorio de PublicacionGuardada para eficiencia)
        // Asegúrate de que el método existsByPublicacionIdPublicacionAndUsuarioIdUsuario exista en PublicacionGuardadaRepository
        boolean alreadySaved = publicacionGuardadaRepository.existsByPublicacionIdPublicacionAndUsuarioIdUsuario(idPublicacion, idUsuario);

        if (!alreadySaved) {
            PublicacionGuardada pg = new PublicacionGuardada();
            // Establecer las relaciones de objeto
            pg.setPublicacion(publicacion);
            pg.setUsuario(usuario);

            // *** ¡AÑADE ESTAS LÍNEAS CRÍTICAS! ***
            // Dado que usas @IdClass y @Id en los Long idUsuario y idPublicacion de PublicacionGuardada,
            // DEBES setear estos campos directamente con los IDs.
            pg.setIdPublicacion(publicacion.getIdPublicacion()); // Asumiendo que Publicacion tiene getIdPublicacion()
            pg.setIdUsuario(usuario.getIdUsuario());             // Asumiendo que Usuario tiene getIdUsuario()
            // ************************************

            // Añade la fecha de guardado (importante si es una columna NOT NULL)
            pg.setFechaGuardado(new Timestamp(System.currentTimeMillis()));

            // Guarda la entidad PublicacionGuardada directamente a través de su repositorio
            publicacionGuardadaRepository.save(pg);
        }
    }

    @Override
    @Transactional
    public void eliminarPublicacionGuardadaPorUsuario(Long idPublicacion, Long idUsuario) {
        Publicacion publicacion = publicacionRepository.findById(idPublicacion)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));

        publicacion.getGuardadosPorUsuarios().removeIf(pg ->
                pg.getUsuario().getIdUsuario().equals(idUsuario) && pg.getPublicacion().getIdPublicacion().equals(idPublicacion)
        );
        publicacionRepository.save(publicacion); // Guarda la publicación para actualizar la relación
    }


}