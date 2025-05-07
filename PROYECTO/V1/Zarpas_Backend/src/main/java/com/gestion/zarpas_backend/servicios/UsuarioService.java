package com.gestion.zarpas_backend.servicios;

import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.modelo.UsuarioRol;

import java.util.Set;

public interface UsuarioService {

    public Usuario guardarUsuario(Usuario usuario, Set<UsuarioRol> usuarioRoles) throws Exception ;

    public Usuario obtenerUsuario(String nombreUsuario)  ;
    public void eliminarUsuario(Long id)  ;
}
