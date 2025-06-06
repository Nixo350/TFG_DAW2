package com.gestion.zarpas_backend.servicio;

import com.gestion.zarpas_backend.modelo.Publicacion;
import com.gestion.zarpas_backend.modelo.ReaccionPublicacion;
import com.gestion.zarpas_backend.modelo.TipoReaccion;
import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.repositorio.PublicacionRepository;
import com.gestion.zarpas_backend.repositorio.ReaccionPublicacionRepository;
import com.gestion.zarpas_backend.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReaccionPublicacionService {

    @Autowired
    private ReaccionPublicacionRepository reaccionPublicacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Transactional
    public ReaccionPublicacion crearOActualizarReaccion(Long idUsuario, Long idPublicacion, TipoReaccion nuevoTipoReaccion) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));
        Publicacion publicacion = publicacionRepository.findById(idPublicacion)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada con ID: " + idPublicacion));

        Optional<ReaccionPublicacion> reaccionExistente = reaccionPublicacionRepository.findById_IdUsuarioAndId_IdPublicacion(idUsuario, idPublicacion);

        if (reaccionExistente.isPresent()) {
            ReaccionPublicacion reaccion = reaccionExistente.get();
            if (reaccion.getTipoReaccion().equals(nuevoTipoReaccion)) {
                // Si la reacción es del mismo tipo, se elimina (deshacer like/dislike)
                reaccionPublicacionRepository.delete(reaccion);
                return null; // Indica que la reacción fue eliminada
            } else {
                // Si la reacción es de tipo diferente, se actualiza
                reaccion.setTipoReaccion(nuevoTipoReaccion);
                reaccion.setFechaReaccion(new Timestamp(System.currentTimeMillis()));
                return reaccionPublicacionRepository.save(reaccion);
            }
        } else {
            // No hay reacción existente, se crea una nueva
            ReaccionPublicacion nuevaReaccion = new ReaccionPublicacion(usuario, publicacion, nuevoTipoReaccion);
            return reaccionPublicacionRepository.save(nuevaReaccion);
        }
    }

    // Obtener el conteo de likes y dislikes para una publicación
    public Map<TipoReaccion, Long> getConteoReaccionesByPublicacionId(Long idPublicacion) {
        List<ReaccionPublicacion> reacciones = reaccionPublicacionRepository.findByPublicacion_IdPublicacion(idPublicacion); // ESTO DEVUELVE UNA LISTA VACÍA
        // ... por lo tanto, el stream.collect devuelve un mapa con 0 en los conteos.
        return reacciones.stream()
                .collect(Collectors.groupingBy(ReaccionPublicacion::getTipoReaccion, Collectors.counting()));
    }

    // Obtener el tipo de reacción de un usuario a una publicación (si existe)
    public Optional<TipoReaccion> getReaccionUsuarioAPublicacion(Long idUsuario, Long idPublicacion) {
        return reaccionPublicacionRepository.findById_IdUsuarioAndId_IdPublicacion(idUsuario, idPublicacion)
                .map(ReaccionPublicacion::getTipoReaccion);
    }
}