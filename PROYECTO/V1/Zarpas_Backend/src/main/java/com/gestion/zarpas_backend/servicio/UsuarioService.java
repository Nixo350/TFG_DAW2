package com.gestion.zarpas_backend.servicio;

import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.modelo.Rol;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UsuarioService {
    Usuario guardarUsuario(Usuario usuario);
    Optional<Usuario> obtenerUsuarioPorId(Long id);
    Optional<Usuario> obtenerUsuarioPorEmail(String email);
    List<Usuario> obtenerTodosLosUsuarios();
    Usuario actualizarUsuario(Usuario usuario);
    void eliminarUsuario(Long id);
    Usuario agregarRolAUsuario(Long usuarioId, Long rolId) throws Exception;
    Set<Rol> obtenerRolesDeUsuario(Long usuarioId); // Quitar 'throws Exception' si el repo no lo lanza
}