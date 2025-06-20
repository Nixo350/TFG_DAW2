package com.gestion.zarpas_backend.servicio;

import com.gestion.zarpas_backend.modelo.UsuarioRol;
import java.util.List;
import java.util.Optional;

public interface UsuarioRolService {
    UsuarioRol guardarUsuarioRol(UsuarioRol usuarioRol);
    Optional<UsuarioRol> obtenerUsuarioRolPorId(Long id);
    List<UsuarioRol> obtenerTodosLosUsuarioRoles();
    void eliminarUsuarioRol(Long id);
    List<UsuarioRol> obtenerRolesPorUsuarioId(Long usuarioId);
    List<UsuarioRol> obtenerUsuariosPorRolId(Long rolId);
}