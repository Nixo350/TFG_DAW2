import { TipoReaccion } from './TipoReaccion'; 
import { Usuario } from './Usuario';
import { Publicacion } from './Publicacion';


export interface ReaccionPublicacionRequest {
  idPublicacion: number;
  idUsuario: number;
  tipoReaccion: TipoReaccion;
}

export interface ReaccionPublicacion {

  usuario: {
    idUsuario: number;
  };
  publicacion: {
    idPublicacion: number;
  };
  tipoReaccion: TipoReaccion | null;
  fechaReaccion?: string; 
}