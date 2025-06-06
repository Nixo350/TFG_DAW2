import { TipoReaccion } from './TipoReaccion'; // Asume que ya tienes este enum
import { Usuario } from './Usuario';
import { Publicacion } from './Publicacion';

export interface ReaccionPublicacion {

  usuario: {
    idUsuario: number;
  };
  publicacion: {
    idPublicacion: number;
  };
  tipoReaccion: TipoReaccion;
  fechaReaccion?: string; // El backend probablemente lo genera
}