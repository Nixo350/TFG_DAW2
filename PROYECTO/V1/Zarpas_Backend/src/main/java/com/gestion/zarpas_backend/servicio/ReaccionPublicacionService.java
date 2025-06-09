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
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + idUsuario));
        Publicacion publicacion = publicacionRepository.findById(idPublicacion)
                .orElseThrow(() -> new IllegalArgumentException("Publicación no encontrada con ID: " + idPublicacion));

        Optional<ReaccionPublicacion> existingReactionOptional = reaccionPublicacionRepository
                .findById_IdUsuarioAndId_IdPublicacion(idUsuario, idPublicacion);

        if (nuevoTipoReaccion == null) {
            if (existingReactionOptional.isPresent()) {
                reaccionPublicacionRepository.delete(existingReactionOptional.get());
                return null; // Indica que la reacción fue eliminada
            } else {
                return null;
            }
        } else {
            if (existingReactionOptional.isPresent()) {
                ReaccionPublicacion reaccion = existingReactionOptional.get();
                if (reaccion.getTipoReaccion().equals(nuevoTipoReaccion)) {
                    reaccionPublicacionRepository.delete(reaccion);
                    return null;
                } else {
                    reaccion.setTipoReaccion(nuevoTipoReaccion);
                    reaccion.setFechaReaccion(new Timestamp(System.currentTimeMillis()));
                    return reaccionPublicacionRepository.save(reaccion);
                }
            } else {
                ReaccionPublicacion nuevaReaccion = new ReaccionPublicacion(usuario, publicacion, nuevoTipoReaccion);
                return reaccionPublicacionRepository.save(nuevaReaccion);
            }
        }
    }

    public Map<TipoReaccion, Long> getConteoReaccionesByPublicacionId(Long idPublicacion) {
        List<ReaccionPublicacion> reacciones = reaccionPublicacionRepository.findByPublicacion_IdPublicacion(idPublicacion);
        return reacciones.stream()
                .collect(Collectors.groupingBy(ReaccionPublicacion::getTipoReaccion, Collectors.counting()));
    }

    public Optional<TipoReaccion> getReaccionUsuarioAPublicacion(Long idUsuario, Long idPublicacion) {
        return reaccionPublicacionRepository.findById_IdUsuarioAndId_IdPublicacion(idUsuario, idPublicacion)
                .map(ReaccionPublicacion::getTipoReaccion);
    }

    @Transactional(readOnly = true)
    public Optional<ReaccionPublicacion> findByPublicacionIdAndUsuarioId(Long idPublicacion, Long idUsuario) {
        return reaccionPublicacionRepository.findByPublicacion_IdPublicacionAndUsuario_IdUsuario(idPublicacion, idUsuario);
    }
}