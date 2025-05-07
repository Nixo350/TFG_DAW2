package com.gestion.zarpas_backend;

import com.gestion.zarpas_backend.modelo.Rol;
import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.modelo.UsuarioRol;
import com.gestion.zarpas_backend.servicios.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class ZarpasBackendApplication implements CommandLineRunner {

    @Autowired
    public UsuarioService usuarioService;

    public static void main(String[] args) {
        SpringApplication.run(ZarpasBackendApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String nombreUsuarioInicial = "Zarpas";
        Usuario usuarioExistente = usuarioService.obtenerUsuario(nombreUsuarioInicial);
        if (usuarioExistente == null) {
        Usuario usuario=new Usuario();
        usuario.setNombre("Zarpas");
        usuario.setEmail("zarpas@gmail.com");
        usuario.setContrasena("1234");
        usuario.setFechaRegistro(Timestamp.valueOf("2025-05-07 15:30:00"));
        usuario.setUltimoLogin(Timestamp.valueOf("2025-05-07 15:30:00"));

        Rol rol=new Rol();
        rol.setNombre("ADMIN");
        rol.setId_rol(1L);

        Set<UsuarioRol> usuarioRols=new HashSet<>();
        UsuarioRol usuarioRol=new UsuarioRol();
        usuarioRol.setRol(rol);
        usuarioRol.setUsuario(usuario);
        usuarioRols.add(usuarioRol);

            try {
                Usuario usuarioGuardado = usuarioService.guardarUsuario(usuario, usuarioRols);
                System.out.println("Usuario inicial guardado: " + usuarioGuardado.getNombre());
            } catch (Exception e) {
                System.err.println("Error al guardar el usuario inicial: " + e.getMessage());
            }
        } else {
            System.out.println("El usuario inicial '" + nombreUsuarioInicial + "' ya existe.");
        }
    }
}
