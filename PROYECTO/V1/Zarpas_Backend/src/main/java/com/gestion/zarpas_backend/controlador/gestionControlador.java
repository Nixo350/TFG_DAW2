package com.gestion.zarpas_backend.controlador;

import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.repositorio.usuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class gestionControlador {

    @Autowired
    private usuarioRepositorio repositorio;

    @GetMapping("/usuarios")
    public List<Usuario> obtenerUsuarios() {
        return repositorio.findAll();
    }
}
