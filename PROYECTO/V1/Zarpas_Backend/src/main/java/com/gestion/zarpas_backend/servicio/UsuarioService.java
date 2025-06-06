package com.gestion.zarpas_backend.servicio;

import com.gestion.zarpas_backend.modelo.Rol;
import com.gestion.zarpas_backend.modelo.Usuario;
import org.springframework.security.crypto.password.PasswordEncoder; // Necesario para la interfaz si el método lo usa directamente

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UsuarioService {
    Usuario guardarUsuario(Usuario usuario);
    List<Usuario> obtenerTodosLosUsuarios();
    Optional<Usuario> obtenerUsuarioPorId(Long id);
    Usuario actualizarUsuario(Long id, Usuario usuarioDetalles);
    void eliminarUsuario(Long id);
    Optional<Usuario> obtenerUsuarioPorEmail(String email);
    Usuario agregarRolAUsuario(Long usuarioId, Long rolId);
    Set<Rol> obtenerRolesDeUsuario(Long usuarioId);
}

