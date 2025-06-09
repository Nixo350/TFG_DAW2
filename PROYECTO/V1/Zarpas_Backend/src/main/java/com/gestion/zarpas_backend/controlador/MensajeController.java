package com.gestion.zarpas_backend.controlador;

import com.gestion.zarpas_backend.modelo.Mensaje;
import com.gestion.zarpas_backend.modelo.Chat;
import com.gestion.zarpas_backend.modelo.Usuario;
import com.gestion.zarpas_backend.servicio.MensajeService;
import com.gestion.zarpas_backend.servicio.ChatService;
import com.gestion.zarpas_backend.servicio.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/mensajes")
public class MensajeController {

    private final MensajeService mensajeService;
    private final ChatService chatService;
    private final UsuarioService usuarioService;

    @Autowired
    public MensajeController(MensajeService mensajeService, ChatService chatService, UsuarioService usuarioService) {
        this.mensajeService = mensajeService;
        this.chatService = chatService;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<Mensaje> crearMensaje(@RequestBody Mensaje mensaje) {
        if (mensaje.getChat() == null || mensaje.getChat().getIdChat() == null ||
                mensaje.getEmisor() == null || mensaje.getEmisor().getIdUsuario() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Chat> chatOpt = chatService.obtenerChatPorId(mensaje.getChat().getIdChat());
        Optional<Usuario> emisorOpt = usuarioService.obtenerUsuarioPorId(mensaje.getEmisor().getIdUsuario());

        if (chatOpt.isPresent() && emisorOpt.isPresent()) {
            mensaje.setChat(chatOpt.get());
            mensaje.setEmisor(emisorOpt.get());
            Mensaje nuevoMensaje = mensajeService.guardarMensaje(mensaje);
            return new ResponseEntity<>(nuevoMensaje, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Mensaje>> obtenerTodosLosMensajes() {
        List<Mensaje> mensajes = mensajeService.obtenerTodosLosMensajes();
        return new ResponseEntity<>(mensajes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mensaje> obtenerMensajePorId(@PathVariable("id") Long id) {
        return mensajeService.obtenerMensajePorId(id)
                .map(mensaje -> new ResponseEntity<>(mensaje, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<List<Mensaje>> obtenerMensajesPorChat(@PathVariable Long chatId) {
        return chatService.obtenerChatPorId(chatId)
                .map(chat -> {
                    List<Mensaje> mensajes = mensajeService.obtenerMensajesPorChat(chat);
                    return new ResponseEntity<>(mensajes, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/emisor/{emisorId}")
    public ResponseEntity<List<Mensaje>> obtenerMensajesEnviadosPorUsuario(@PathVariable Long emisorId) {
        return usuarioService.obtenerUsuarioPorId(emisorId)
                .map(emisor -> {
                    List<Mensaje> mensajes = mensajeService.obtenerMensajesEnviadosPorUsuario(emisor);
                    return new ResponseEntity<>(mensajes, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mensaje> actualizarMensaje(@PathVariable("id") Long id, @RequestBody Mensaje mensaje) {
        if (!id.equals(mensaje.getIdMensaje())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            Mensaje mensajeActualizado = mensajeService.actualizarMensaje(mensaje);
            return new ResponseEntity<>(mensajeActualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMensaje(@PathVariable("id") Long id) {
        try {
            mensajeService.eliminarMensaje(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}/leido")
    public ResponseEntity<Mensaje> marcarMensajeComoLeido(@PathVariable("id") Long id) {
        try {
            Mensaje mensajeLeido = mensajeService.marcarMensajeComoLeido(id);
            return new ResponseEntity<>(mensajeLeido, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}