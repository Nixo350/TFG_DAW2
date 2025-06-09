package com.gestion.zarpas_backend.servicio.impl;

import com.gestion.zarpas_backend.modelo.PublicacionGuardada;
import com.gestion.zarpas_backend.repositorio.PublicacionGuardadaRepository;
import com.gestion.zarpas_backend.repositorio.UsuarioRepository;
import com.gestion.zarpas_backend.repositorio.PublicacionRepository;
import com.gestion.zarpas_backend.servicio.PublicacionGuardadaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PublicacionGuardadaServiceImpl implements PublicacionGuardadaService {

    private final PublicacionGuardadaRepository publicacionGuardadaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PublicacionRepository publicacionRepository;

    @Autowired
    public PublicacionGuardadaServiceImpl(PublicacionGuardadaRepository publicacionGuardadaRepository,
                                          UsuarioRepository usuarioRepository,
                                          PublicacionRepository publicacionRepository) {
        this.publicacionGuardadaRepository = publicacionGuardadaRepository;
        this.usuarioRepository = usuarioRepository;
        this.publicacionRepository = publicacionRepository;
    }



    public List<PublicacionGuardada> getPublicacionesGuardadasByUsuario(Long idUsuario) {
        return publicacionGuardadaRepository.findByUsuario_IdUsuario(idUsuario);
    }




}