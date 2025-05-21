package com.gestion.zarpas_backend.servicio;

import com.gestion.zarpas_backend.modelo.Rol;
import java.util.List;
import java.util.Optional;

public interface RolService {
    Rol guardarRol(Rol rol);
    Optional<Rol> obtenerRolPorId(Long id);
    List<Rol> obtenerTodosLosRoles();
    Rol actualizarRol(Rol rol);
    void eliminarRol(Long id);
    Optional<Rol> obtenerRolPorNombre(String nombre); // Añadido
}