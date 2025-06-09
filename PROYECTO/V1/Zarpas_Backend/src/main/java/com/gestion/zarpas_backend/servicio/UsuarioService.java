package com.gestion.zarpas_backend.servicio;

import com.gestion.zarpas_backend.dto.UsuarioPerfilUpdateRequest;
import com.gestion.zarpas_backend.modelo.Rol;
import com.gestion.zarpas_backend.modelo.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UsuarioService {
    Usuario guardarUsuario(Usuario usuario);
    List<Usuario> obtenerTodosLosUsuarios();
    Optional<Usuario> obtenerUsuarioPorId(Long id);
    void eliminarUsuario(Long id);
    Optional<Usuario> obtenerUsuarioPorEmail(String email);
    Usuario agregarRolAUsuario(Long usuarioId, Long rolId);
    Set<Rol> obtenerRolesDeUsuario(Long usuarioId);
    Optional<Usuario> findByUsername(String username);
    void changePassword(Long idUsuario, String newContrasena);
    Usuario updateUsuarioPerfil(Long idUsuario, UsuarioPerfilUpdateRequest request);
}

