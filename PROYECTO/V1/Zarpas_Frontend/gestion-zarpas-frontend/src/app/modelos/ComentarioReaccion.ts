import { TipoReaccion } from "./TipoReaccion";

export interface ComentarioReaccion {
    idUsuario: number;
    idComentario: number;
    tipoReaccion: TipoReaccion;
    fechaReaccion?: Date | null;
  }
  
  // Opcional, si necesitas la clase Id por separado
  export interface ComentarioReaccionRequest {
    idUsuario: number;
    idComentario: number;
    tipoReaccion: TipoReaccion;
  }