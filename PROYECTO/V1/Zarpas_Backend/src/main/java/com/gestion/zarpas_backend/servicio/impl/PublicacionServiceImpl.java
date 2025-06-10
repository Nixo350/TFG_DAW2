package com.gestion.zarpas_backend.servicio.impl;

import com.gestion.zarpas_backend.modelo.Categoria;
import com.gestion.zarpas_backend.modelo.Publicacion;
import com.gestion.zarpas_backend.modelo.PublicacionGuardada;
import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.repositorio.*;
import com.gestion.zarpas_backend.servicio.PublicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
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
        if (publicacion.getFechaCreacion() == null) {
            publicacion.setFechaCreacion(Timestamp.from(Instant.now()));
        }
        publicacion.setFechaModificacion(Timestamp.from(Instant.now()));

        if (publicacion.getCategoria() != null && publicacion.getCategoria().getNombre() != null) {
            String nombreCategoria = publicacion.getCategoria().getNombre();
            Optional<Categoria> existingCategory = categoriaRepository.findByNombreIgnoreCase(nombreCategoria);
            if (existingCategory.isPresent()) {
                publicacion.setCategoria(existingCategory.get());
            } else {
                Categoria nuevaCategoria = new Categoria();
                nuevaCategoria.setNombre(nombreCategoria);
                publicacion.setCategoria(categoriaRepository.save(nuevaCategoria));
            }
        }
        return publicacionRepository.save(publicacion);
    }


    @Override
    @Transactional(readOnly = true)
    public List<Publicacion> obtenerTodasLasPublicaciones() {
        return publicacionRepository.findAllByOrderByFechaCreacionDesc();
    }




    @Override
    @Transactional
    public List<Publicacion> searchPublicaciones(String keyword) {
        return publicacionRepository.searchByKeyword(keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Publicacion> getPublicacionesByCategoria(String nombreCategoria) {
        return publicacionRepository.findByCategoria_NombreIgnoreCaseOrderByFechaCreacionDesc(nombreCategoria);
    }

    @Override
    public List<Categoria> getAllCategorias() {
        return categoriaRepository.findAll();
    }

    @Override
    @Transactional
    public Categoria crearCategoria(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }



    @Override
    @Transactional
    public void guardarPublicacionPorUsuario(Long idPublicacion, Long idUsuario) {
        Publicacion publicacion = publicacionRepository.findById(idPublicacion)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean alreadySaved = publicacionGuardadaRepository.existsByPublicacionIdPublicacionAndUsuarioIdUsuario(idPublicacion, idUsuario);

        if (!alreadySaved) {
            PublicacionGuardada pg = new PublicacionGuardada();
            pg.setPublicacion(publicacion);
            pg.setUsuario(usuario);

            pg.setIdPublicacion(publicacion.getIdPublicacion());
            pg.setIdUsuario(usuario.getIdUsuario());

            pg.setFechaGuardado(new Timestamp(System.currentTimeMillis()));

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
        publicacionRepository.save(publicacion);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPublicacionOwner(Long publicacionId, Long userId) {
        return publicacionRepository.findById(publicacionId)
                .map(publicacion -> publicacion.getUsuario() != null && publicacion.getUsuario().getIdUsuario().equals(userId))
                .orElse(false); // Si la publicación no existe o no tiene un usuario, no es propietario
    }
    @Override
    @Transactional // Esta operación modifica datos (elimina)
    public void deletePublicacion(Long id) {
        // Opcional: Primero verifica si la publicación existe antes de intentar eliminarla
        if (!publicacionRepository.existsById(id)) {
            throw new NoSuchElementException("Publicación con ID " + id + " no encontrada para eliminar.");
        }
        publicacionRepository.deleteById(id);
    }
    @Override
    @Transactional(readOnly = true) // Esta operación solo lee datos
    public List<Publicacion> getPublicacionesByUserId(Long userId) {
        // Llama al método del repositorio para obtener las publicaciones
        return publicacionRepository.findByUsuarioIdUsuario(userId);
    }

}