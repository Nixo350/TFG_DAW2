package com.gestion.zarpas_backend.servicio.impl;

import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.modelo.Rol;
import com.gestion.zarpas_backend.modelo.UsuarioRol;
import com.gestion.zarpas_backend.repositorio.UsuarioRepository;
import com.gestion.zarpas_backend.repositorio.RolRepository;
import com.gestion.zarpas_backend.repositorio.UsuarioRolRepository;
import com.gestion.zarpas_backend.servicio.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, RolRepository rolRepository, UsuarioRolRepository usuarioRolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
    }

    @Override
    @Transactional
    public Usuario guardarUsuario(Usuario usuario) {
        if (usuario.getFechaRegistro() == null) {
            usuario.setFechaRegistro(Timestamp.from(Instant.now()));
        }
        if (usuario.getUltimoLogin() == null) {
            usuario.setUltimoLogin(Timestamp.from(Instant.now()));
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional
    public Usuario actualizarUsuario(Usuario usuario) {
        // Asegúrate de que el usuario exista antes de actualizar
        return usuarioRepository.findById(usuario.getIdUsuario())
                .map(existingUser -> {
                    existingUser.setEmail(usuario.getEmail());
                    existingUser.setNombre(usuario.getNombre());
                    existingUser.setContrasena(usuario.getContrasena());
                    existingUser.setFotoPerfil(usuario.getFotoPerfil());
                    existingUser.setUltimoLogin(Timestamp.from(Instant.now()));
                    // Puedes añadir lógica para actualizar roles si es necesario
                    return usuarioRepository.save(existingUser);
                }).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuario.getIdUsuario()));
    }

    @Override
    @Transactional
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Usuario agregarRolAUsuario(Long usuarioId, Long rolId) throws Exception {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + rolId));

        // Verificar si el usuario ya tiene este rol
        boolean rolYaAsignado = usuario.getUsuarioRoles().stream()
                .anyMatch(ur -> ur.getRol().getId().equals(rolId));
        if (rolYaAsignado) {
            throw new RuntimeException("El usuario ya tiene asignado este rol.");
        }

        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setUsuario(usuario);
        usuarioRol.setRol(rol);
        usuarioRolRepository.save(usuarioRol);

        // Asegurarse de que la colección en el usuario esté actualizada
        usuario.getUsuarioRoles().add(usuarioRol);
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Rol> obtenerRolesDeUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .map(usuario -> usuario.getUsuarioRoles().stream()
                        .map(UsuarioRol::getRol)
                        .collect(Collectors.toSet()))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));
    }
}