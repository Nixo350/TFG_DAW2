import { Chat } from "./Chat";
import { Comentario } from "./Comentario";
import { ComentarioReaccion } from "./ComentarioReaccion";
import { Mensaje } from "./Mensaje";
import { Publicacion } from "./Publicacion";
import { PublicacionGuardada } from "./PublicacionGuardada";

export interface Usuario {
    idUsuario: number;
    email: string;
    nombre: string;
    contrasena: string;
    fechaRegistro: Date;
    ultimoLogin: Date;
    publicaciones: Publicacion[];
    comentarios: Comentario[];
    chats: Chat[];
    mensajesEnviados: Mensaje[];
    publicacionesGuardadas: PublicacionGuardada[];
    comentariosReaccionados: ComentarioReaccion[];
  }