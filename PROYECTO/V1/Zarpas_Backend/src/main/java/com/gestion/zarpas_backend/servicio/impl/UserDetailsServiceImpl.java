package com.gestion.zarpas_backend.servicio.impl;

import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.repositorio.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busca el usuario en tu repositorio
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username: " + username));

        // ¡Esta es la línea clave! Debe devolver tu UserDetailsImpl personalizado.
        // Asumiendo que tienes un método estático 'build' en UserDetailsImpl
        // que toma un objeto Usuario y lo convierte a UserDetailsImpl.
        return UserDetailsImpl.build(usuario);
    }
}