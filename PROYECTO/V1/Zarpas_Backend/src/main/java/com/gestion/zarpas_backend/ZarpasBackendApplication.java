package com.gestion.zarpas_backend;

import com.gestion.zarpas_backend.modelo.Rol;
import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.servicio.UsuarioService;
import com.gestion.zarpas_backend.servicio.RolService; // Necesitamos el servicio de Rol para obtener el rol "ADMIN"
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional; // Importar Optional

@SpringBootApplication
public class ZarpasBackendApplication implements CommandLineRunner {

    private final UsuarioService usuarioService;
    private final RolService rolService; // Inyectar RolService

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
        String emailUsuarioInicial = "zarpas@gmail.com"; // Usaremos el email como identificador único
        String nombreUsuarioInicial = "Zarpas";

        // 1. Verificar si el usuario ya existe por email
        Optional<Usuario> usuarioExistenteOpt = usuarioService.obtenerUsuarioPorEmail(emailUsuarioInicial);

        if (usuarioExistenteOpt.isEmpty()) { // Si el usuario no existe
            System.out.println("Creando usuario inicial: " + nombreUsuarioInicial);

            Usuario usuario = new Usuario();
            usuario.setNombre(nombreUsuarioInicial);
            usuario.setEmail(emailUsuarioInicial);
            usuario.setContrasena("1234"); // Considera usar un codificador de contraseñas (PasswordEncoder) en una app real
            usuario.setFechaRegistro(Timestamp.from(Instant.now()));
            usuario.setUltimoLogin(Timestamp.from(Instant.now()));

            // 2. Guardar el usuario (sin roles por ahora)
            Usuario usuarioGuardado = usuarioService.guardarUsuario(usuario);
            System.out.println("Usuario guardado: " + usuarioGuardado.getNombre());

            // 3. Obtener o crear el rol "ADMIN"
            Optional<Rol> rolAdminOpt = rolService.obtenerRolPorNombre("ADMIN");
            Rol rolAdmin;
            if (rolAdminOpt.isEmpty()) {
                rolAdmin = new Rol();
                rolAdmin.setNombre("ADMIN");
                rolAdmin = rolService.guardarRol(rolAdmin); // Guardar el rol si no existe
                System.out.println("Rol 'ADMIN' creado.");
            } else {
                rolAdmin = rolAdminOpt.get();
                System.out.println("Rol 'ADMIN' existente.");
            }

            // 4. Asignar el rol "ADMIN" al usuario recién creado
            try {
                usuarioService.agregarRolAUsuario(usuarioGuardado.getIdUsuario(), rolAdmin.getId()); // Changed getId_rol() to getId()
                System.out.println("Rol 'ADMIN' asignado a " + usuarioGuardado.getNombre());
            } catch (RuntimeException e) {
                System.err.println("Error al asignar el rol 'ADMIN' al usuario: " + e.getMessage());
            }

        } else {
            System.out.println("El usuario inicial '" + nombreUsuarioInicial + "' (Email: " + emailUsuarioInicial + ") ya existe.");
            // Si el usuario ya existe, puedes realizar otras operaciones, como verificar si tiene el rol "ADMIN"
            Usuario usuarioExistente = usuarioExistenteOpt.get();
            try {
                boolean tieneAdminRol = usuarioExistente.getUsuarioRoles().stream()
                        .anyMatch(ur -> "ADMIN".equals(ur.getRol().getNombre()));
                if (!tieneAdminRol) {
                    System.out.println("El usuario existente no tiene el rol 'ADMIN'. Intentando asignarlo...");
                    Optional<Rol> rolAdminOpt = rolService.obtenerRolPorNombre("ADMIN");
                    if (rolAdminOpt.isPresent()) {
                        usuarioService.agregarRolAUsuario(usuarioExistente.getIdUsuario(), rolAdminOpt.get().getId()); // Changed getId_rol() to getId()
                        System.out.println("Rol 'ADMIN' asignado a " + usuarioExistente.getNombre());
                    } else {
                        System.out.println("No se pudo asignar el rol 'ADMIN': el rol no existe en la base de datos.");
                    }
                }
            } catch (Exception e) {
                System.err.println("Error al verificar/asignar rol al usuario existente: " + e.getMessage());
            }
        }
    }
}