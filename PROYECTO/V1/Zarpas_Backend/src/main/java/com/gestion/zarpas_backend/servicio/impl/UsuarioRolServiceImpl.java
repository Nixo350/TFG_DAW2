package com.gestion.zarpas_backend.servicio.impl;

import com.gestion.zarpas_backend.modelo.UsuarioRol;
import com.gestion.zarpas_backend.repositorio.UsuarioRolRepository;
import com.gestion.zarpas_backend.servicio.UsuarioRolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioRolServiceImpl implements UsuarioRolService {

    private final UsuarioRolRepository usuarioRolRepository;

    @Autowired
    public UsuarioRolServiceImpl(UsuarioRolRepository usuarioRolRepository) {
        this.usuarioRolRepository = usuarioRolRepository;
    }

    @Override
    @Transactional
    public UsuarioRol guardarUsuarioRol(UsuarioRol usuarioRol) {
        return usuarioRolRepository.save(usuarioRol);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioRol> obtenerUsuarioRolPorId(Long id) { // ID simple
        return usuarioRolRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioRol> obtenerTodosLosUsuarioRoles() {
        return usuarioRolRepository.findAll();
    }

    @Override
    @Transactional
    public void eliminarUsuarioRol(Long id) { // ID simple
        usuarioRolRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioRol> obtenerRolesPorUsuarioId(Long usuarioId) {
        return usuarioRolRepository.findByUsuario_IdUsuario(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioRol> obtenerUsuariosPorRolId(Long rolId) {
        return usuarioRolRepository.findByRol_Id(rolId);
    }
}