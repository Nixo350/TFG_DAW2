package com.gestion.zarpas_backend.controlador;

import com.gestion.zarpas_backend.modelo.Chat;
import com.gestion.zarpas_backend.servicio.ChatService;
import com.gestion.zarpas_backend.servicio.UsuarioChatService;
import com.gestion.zarpas_backend.servicio.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;
    private final UsuarioService usuarioService;
    private final UsuarioChatService usuarioChatService;

    @Autowired
    public ChatController(ChatService chatService, UsuarioService usuarioService, UsuarioChatService usuarioChatService) {
        this.chatService = chatService;
        this.usuarioService = usuarioService;
        this.usuarioChatService = usuarioChatService;
    }

    @PostMapping
    public ResponseEntity<Chat> crearChat(@RequestBody Chat chat) {
        Chat nuevoChat = chatService.guardarChat(chat);
        return new ResponseEntity<>(nuevoChat, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Chat>> obtenerTodosLosChats() {
        List<Chat> chats = chatService.obtenerTodosLosChats();
        return new ResponseEntity<>(chats, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Chat> obtenerChatPorId(@PathVariable("id") Long id) {
        return chatService.obtenerChatPorId(id)
                .map(chat -> new ResponseEntity<>(chat, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Chat> actualizarChat(@PathVariable("id") Long id, @RequestBody Chat chat) {
        if (!id.equals(chat.getIdChat())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            Chat chatActualizado = chatService.actualizarChat(chat);
            return new ResponseEntity<>(chatActualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarChat(@PathVariable("id") Long id) {
        try {
            chatService.eliminarChat(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Chat>> obtenerChatsPorUsuarioId(@PathVariable Long usuarioId) {
        try {
            List<Chat> chats = chatService.obtenerChatsPorUsuarioId(usuarioId);
            return new ResponseEntity<>(chats, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{chatId}/usuarios/{usuarioId}")
    public ResponseEntity<Void> unirUsuarioAchat(@PathVariable Long chatId, @PathVariable Long usuarioId) {
        try {
            usuarioChatService.unirUsuarioAchat(usuarioId, chatId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{chatId}/usuarios/{usuarioId}")
    public ResponseEntity<Void> sacarUsuarioDeChat(@PathVariable Long chatId, @PathVariable Long usuarioId) {
        try {
            usuarioChatService.sacarUsuarioDeChat(usuarioId, chatId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}