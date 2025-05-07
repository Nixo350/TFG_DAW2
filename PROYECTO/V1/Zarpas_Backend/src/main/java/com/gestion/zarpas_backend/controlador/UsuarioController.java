package com.gestion.zarpas_backend.controlador;

import com.gestion.zarpas_backend.modelo.Rol;
import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.modelo.UsuarioRol;
import com.gestion.zarpas_backend.servicios.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/")
    public Usuario guardarUsuario(@RequestBody Usuario usuario) throws Exception {

        Set<UsuarioRol> roles =new HashSet<>();

        Rol rol = new Rol();
        rol.setNombre("NORMAL");
        rol.setId_rol(2L);

        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setRol(rol);
        usuarioRol.setUsuario(usuario);

        return usuarioService.guardarUsuario(usuario,roles);

    }
    @GetMapping("/{nombre}")
    public Usuario obtenerUsuario(@PathVariable("nombre") String nombre) throws Exception {
        return usuarioService.obtenerUsuario(nombre);
    }

    @DeleteMapping("/{id}")
    public void eliminarUsuario(@PathVariable("id") Long id) throws Exception {
        usuarioService.eliminarUsuario(id);
    }

}
