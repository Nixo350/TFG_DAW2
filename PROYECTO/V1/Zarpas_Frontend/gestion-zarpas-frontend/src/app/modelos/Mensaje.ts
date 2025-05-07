import { Chat } from "./Chat";
import { Usuario } from "./Usuario";

export interface Mensaje {
    idMensaje: number;
    chat: Chat;
    emisor: Usuario;
    contenido: string;
    fechaEnvio: Date;
    leido: boolean;
  }