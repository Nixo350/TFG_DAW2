package com.gestion.zarpas_backend.servicio.impl;

import com.gestion.zarpas_backend.modelo.Rol;
import com.gestion.zarpas_backend.repositorio.RolRepository;
import com.gestion.zarpas_backend.servicio.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;

    @Autowired
    public RolServiceImpl(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Override
    @Transactional
    public Rol guardarRol(Rol rol) {
        return rolRepository.save(rol);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rol> obtenerRolPorId(Long id) {
        return rolRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rol> obtenerTodosLosRoles() {
        return rolRepository.findAll();
    }

    @Override
    @Transactional
    public Rol actualizarRol(Rol rol) {
        return rolRepository.findById(rol.getId())
                .map(existingRol -> {
                    existingRol.setNombre(rol.getNombre());
                    // No hay más campos para actualizar en Rol
                    return rolRepository.save(existingRol);
                }).orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + rol.getId()));
    }

    @Override
    @Transactional
    public void eliminarRol(Long id) {
        rolRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rol> obtenerRolPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre);
    }
}