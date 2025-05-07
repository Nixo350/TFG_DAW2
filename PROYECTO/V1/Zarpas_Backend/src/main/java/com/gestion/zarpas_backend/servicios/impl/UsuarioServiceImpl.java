package com.gestion.zarpas_backend.servicios.impl;

import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.modelo.UsuarioRol;
import com.gestion.zarpas_backend.repositorio.rolRepositorio;
import com.gestion.zarpas_backend.repositorio.usuarioRepositorio;
import com.gestion.zarpas_backend.servicios.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private usuarioRepositorio usuarioRepositorio;

    @Autowired
    private rolRepositorio rolRepositorio;

    @Override
    public Usuario guardarUsuario(Usuario usuario, Set<UsuarioRol> usuarioRoles) throws Exception {
        Usuario usuarioLocal=usuarioRepositorio.findBynombre(usuario.getNombre());
        if(usuarioLocal!=null){
            System.out.println("El usuario ya existe");
            throw new Exception("El usuario ya esta presente");
        }
        else{
            for(UsuarioRol usuarioRol:usuarioRoles){
                rolRepositorio.save(usuarioRol.getRol());
            }
            usuario.getUsuarioRoles().addAll(usuarioRoles);
            usuarioLocal=usuarioRepositorio.save(usuario);
        }
        return usuarioLocal;
    }

    @Override
    public Usuario obtenerUsuario(String nombreUsuario){
        return usuarioRepositorio.findBynombre(nombreUsuario);
    }

    @Override
    public void eliminarUsuario(Long id) {
        usuarioRepositorio.deleteById(id);

    }
}
