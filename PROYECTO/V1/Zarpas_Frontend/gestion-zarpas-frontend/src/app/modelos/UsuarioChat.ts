import { Chat } from "./Chat";
import { Usuario } from "./Usuario";

export interface UsuarioChat {
    idUsuario: number;
    idChat: number;
    fechaUnion: Date;
    usuario: Usuario;
    chat: Chat;
  }
  

  export interface UsuarioChatId {
    idUsuario: number;
    idChat: number;
  }