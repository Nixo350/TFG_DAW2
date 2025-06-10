package com.gestion.zarpas_backend;

import com.gestion.zarpas_backend.modelo.Rol;
import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.servicio.UsuarioService;
import com.gestion.zarpas_backend.servicio.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@SpringBootApplication
public class ZarpasBackendApplication implements CommandLineRunner {

    private final UsuarioService usuarioService;
    private final RolService rolService;

    @Autowired
    public ZarpasBackendApplication(UsuarioService usuarioService, RolService rolService) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
    }

    public static void main(String[] args) {
        SpringApplication.run(ZarpasBackendApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        /*String emailUsuarioInicial = "zarpas@gmail.com";
        String nombreUsuarioInicial = "Zarpas";

        Optional<Usuario> usuarioExistenteOpt = usuarioService.obtenerUsuarioPorEmail(emailUsuarioInicial);

        if (usuarioExistenteOpt.isEmpty()) { // Si el usuario no existe
            System.out.println("Creando usuario inicial: " + nombreUsuarioInicial);

            Usuario usuario = new Usuario();
            usuario.setNombre(nombreUsuarioInicial);
            usuario.setEmail(emailUsuarioInicial);
            usuario.setContrasena("1234");
            usuario.setFechaRegistro(Timestamp.from(Instant.now()));
            usuario.setUltimoLogin(Timestamp.from(Instant.now()));

            Usuario usuarioGuardado = usuarioService.guardarUsuario(usuario);
            System.out.println("Usuario guardado: " + usuarioGuardado.getNombre());

            Optional<Rol> rolAdminOpt = rolService.obtenerRolPorNombre("ADMIN");
            Rol rolAdmin;
            if (rolAdminOpt.isEmpty()) {
                rolAdmin = new Rol();
                rolAdmin.setNombre("ADMIN");
                rolAdmin = rolService.guardarRol(rolAdmin);
                System.out.println("Rol 'ADMIN' creado.");
            } else {
                rolAdmin = rolAdminOpt.get();
                System.out.println("Rol 'ADMIN' existente.");
            }

            try {
                usuarioService.agregarRolAUsuario(usuarioGuardado.getIdUsuario(), rolAdmin.getId());
                System.out.println("Rol 'ADMIN' asignado a " + usuarioGuardado.getNombre());
            } catch (RuntimeException e) {
                System.err.println("Error al asignar el rol 'ADMIN' al usuario: " + e.getMessage());
            }

        } else {
            System.out.println("El usuario inicial '" + nombreUsuarioInicial + "' (Email: " + emailUsuarioInicial + ") ya existe.");
            Usuario usuarioExistente = usuarioExistenteOpt.get();
            try {
                boolean tieneAdminRol = usuarioExistente.getUsuarioRoles().stream()
                        .anyMatch(ur -> "ADMIN".equals(ur.getRol().getNombre()));
                if (!tieneAdminRol) {
                    System.out.println("El usuario existente no tiene el rol 'ADMIN'. Intentando asignarlo...");
                    Optional<Rol> rolAdminOpt = rolService.obtenerRolPorNombre("ADMIN");
                    if (rolAdminOpt.isPresent()) {
                        usuarioService.agregarRolAUsuario(usuarioExistente.getIdUsuario(), rolAdminOpt.get().getId());
                        System.out.println("Rol 'ADMIN' asignado a " + usuarioExistente.getNombre());
                    } else {
                        System.out.println("No se pudo asignar el rol 'ADMIN': el rol no existe en la base de datos.");
                    }
                }
            } catch (Exception e) {
                System.err.println("Error al verificar/asignar rol al usuario existente: " + e.getMessage());
            }
        }*/
    }
}