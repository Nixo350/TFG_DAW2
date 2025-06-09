package com.gestion.zarpas_backend.servicio.impl;

import com.gestion.zarpas_backend.dto.UsuarioPerfilUpdateRequest;
import com.gestion.zarpas_backend.modelo.Rol;
import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.modelo.UsuarioRol;
import com.gestion.zarpas_backend.repositorio.RolRepository;
import com.gestion.zarpas_backend.repositorio.UsuarioRepository;
import com.gestion.zarpas_backend.servicio.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, RolRepository rolRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public Usuario guardarUsuario(Usuario usuario) {

        if (usuario.getFechaRegistro() == null) {
            usuario.setFechaRegistro(new Timestamp(System.currentTimeMillis()));
        }

        if (usuario.getUsuarioRoles() == null || usuario.getUsuarioRoles().isEmpty()) {
            Rol userRole = rolRepository.findByNombre("USER")
                    .orElseThrow(() -> new RuntimeException("Error: El rol 'USER' no se encuentra."));
            Set<UsuarioRol> usuarioRoles = new HashSet<>();
            usuarioRoles.add(new UsuarioRol(null, usuario, userRole));
            usuario.setUsuarioRoles(usuarioRoles);
        }

        if (usuario.getUsuarioRoles() != null) {
            for (UsuarioRol ur : usuario.getUsuarioRoles()) {
                ur.setUsuario(usuario);
            }
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    @Transactional
    public Usuario updateUsuarioPerfil(Long idUsuario, UsuarioPerfilUpdateRequest request) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con ID: " + idUsuario));

        if (!usuario.getUsername().equals(request.getUsername()) && usuarioRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Error: ¡El nombre de usuario '" + request.getUsername() + "' ya está en uso!");
        }

        usuario.setUsername(request.getUsername());
        usuario.setFotoPerfil(request.getFotoPerfil());

        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void changePassword(Long idUsuario, String newContrasena) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con ID: " + idUsuario));

        String encodedPassword = passwordEncoder.encode(newContrasena);
        usuario.setContrasena(encodedPassword);

        usuarioRepository.save(usuario);
    }

    @Override
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con id: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    public Optional<Usuario> obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public Usuario agregarRolAUsuario(Long usuarioId, Long rolId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + usuarioId));
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + rolId));

        if (usuario.getUsuarioRoles().stream().anyMatch(ur -> ur.getRol().equals(rol))) {
            throw new RuntimeException("El rol ya está asignado al usuario.");
        }

        UsuarioRol usuarioRol = new UsuarioRol(usuario, rol);
        usuario.getUsuarioRoles().add(usuarioRol);
        return usuarioRepository.save(usuario);
    }

    @Override
    public Set<Rol> obtenerRolesDeUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + usuarioId));

        Set<Rol> roles = new HashSet<>();
        for (UsuarioRol usuarioRol : usuario.getUsuarioRoles()) {
            roles.add(usuarioRol.getRol());
        }
        return roles;
    }
}