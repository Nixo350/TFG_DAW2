import { Chat } from "./Chat";
import { Usuario } from "./Usuario";

export interface UsuarioChat {
    idUsuario: number;
    idChat: number;
    fechaUnion: Date;
    usuario: Usuario;
    chat: Chat;
  }
  
  // Opcional, si necesitas la clase Id por separado
  export interface UsuarioChatId {
    idUsuario: number;
    idChat: number;
  }