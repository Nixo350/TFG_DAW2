import { Comentario } from "./Comentario";
import { TipoReaccion } from "./TipoReaccion";
import { Usuario } from "./Usuario";

export interface ComentarioReaccion {
    idUsuario: number;
    idComentario: number;
    tipoReaccion: TipoReaccion;
    fechaReaccion: Date;
    usuario: Usuario;
    comentario: Comentario;
  }
  
  // Opcional, si necesitas la clase Id por separado
  export interface ComentarioReaccionId {
    idUsuario: number;
    idComentario: number;
  }