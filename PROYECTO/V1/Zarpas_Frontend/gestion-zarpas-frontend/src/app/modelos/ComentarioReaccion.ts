import { TipoReaccion } from "./TipoReaccion";

export interface ComentarioReaccion {
    idUsuario: number;
    idComentario: number;
    tipoReaccion: TipoReaccion;
    fechaReaccion?: Date | null;
  }
  export interface ComentarioReaccionRequest {
    idUsuario: number;
    idComentario: number;
    tipoReaccion: TipoReaccion | null;
  }