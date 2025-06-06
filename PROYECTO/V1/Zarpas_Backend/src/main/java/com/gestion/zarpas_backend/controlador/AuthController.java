package com.gestion.zarpas_backend.controlador;

import com.gestion.zarpas_backend.modelo.Rol;
import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.modelo.UsuarioRol;
import com.gestion.zarpas_backend.repositorio.RolRepository;
import com.gestion.zarpas_backend.repositorio.UsuarioRepository;
import com.gestion.zarpas_backend.request.LoginRequest;
import com.gestion.zarpas_backend.request.SignupRequest; // <<-- ¡¡AÑADE ESTA LÍNEA!!
import com.gestion.zarpas_backend.response.JwtResponse;
import com.gestion.zarpas_backend.response.MessageResponse;
import com.gestion.zarpas_backend.seguridad.jwt.JwtUtils;
import com.gestion.zarpas_backend.servicio.UsuarioService;
import com.gestion.zarpas_backend.servicio.impl.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UsuarioService usuarioService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (usuarioRepository.existsByUsername(signUpRequest.getUsername())) {
            // Devuelve un JSON para errores también
            return new ResponseEntity<>(new MessageResponse("Error: ¡El nombre de usuario ya está en uso!"), HttpStatus.CONFLICT);
        }

        if (usuarioRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<>(new MessageResponse("Error: ¡El correo electrónico ya está en uso!"), HttpStatus.CONFLICT);
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(signUpRequest.getUsername());
        usuario.setEmail(signUpRequest.getEmail());
        usuario.setContrasena(encoder.encode(signUpRequest.getContrasena()));
        usuario.setNombre(signUpRequest.getNombre());
        usuario.setFechaRegistro(new Timestamp(System.currentTimeMillis()));

        Set<UsuarioRol> usuarioRoles = new HashSet<>();
        Rol userRole = rolRepository.findByNombre("USER")
                .orElseThrow(() -> new RuntimeException("Error: El rol 'USER' no se encuentra."));
        usuarioRoles.add(new UsuarioRol(usuario, userRole));

        usuario.setUsuarioRoles(usuarioRoles);

        usuarioService.guardarUsuario(usuario);

        return new ResponseEntity<>(new MessageResponse("Usuario registrado exitosamente!"), HttpStatus.OK);
    }
}