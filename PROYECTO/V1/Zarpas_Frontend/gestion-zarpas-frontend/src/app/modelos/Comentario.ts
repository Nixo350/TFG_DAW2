import { TipoReaccion } from "./TipoReaccion";
import { Usuario } from "./Usuario";

export interface Comentario {
    idComentario?: number;
    usuario: Usuario;
    usernameUsuario?: string;
    idPublicacion?: number;
    texto: string;
    fechaCreacion?: Date | null;
  fechaModificacion?: Date | null;
  reaccionesConteo?: { like: number; dislike: number };
  currentUserReaction?: TipoReaccion | null;
  }

export interface ComentarioRequest {
    idUsuario: number;
    idPublicacion: number;
    texto: string;
  }