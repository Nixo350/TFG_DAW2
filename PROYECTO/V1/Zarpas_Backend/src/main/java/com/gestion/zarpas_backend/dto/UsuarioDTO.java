package com.gestion.zarpas_backend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import com.gestion.zarpas_backend.modelo.Usuario;
import java.util.stream.Collectors;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long idUsuario;
    private String username;
    private String email;
    private String nombre;
    private String fotoPerfil;
    private List<PublicacionGuardadaDTO> publicacionesGuardadas;

    public UsuarioDTO(Usuario usuario) {
        this.idUsuario = usuario.getIdUsuario();
        this.username = usuario.getUsername();
        this.email = usuario.getEmail();
        this.nombre = usuario.getNombre();
        this.fotoPerfil = usuario.getFotoPerfil();

        if (usuario.getPublicacionesGuardadas() != null) {
            this.publicacionesGuardadas = usuario.getPublicacionesGuardadas().stream()
                    .map(pg -> {
                        LocalDateTime fechaGuardadoDelPost = ((Timestamp) pg.getFechaGuardado()).toLocalDateTime();
                        return new PublicacionGuardadaDTO(pg.getIdPublicacion(), fechaGuardadoDelPost);
                    })
                    .collect(Collectors.toList());
        }
    }
}