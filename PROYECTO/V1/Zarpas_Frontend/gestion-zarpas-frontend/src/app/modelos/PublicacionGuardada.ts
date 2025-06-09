
import { Publicacion } from './Publicacion';
import { Usuario } from './Usuario';

export interface PublicacionGuardada {
  idPublicacionGuardada?: number; 
  publicacion?: { idPublicacion: number }; 
  usuario?: { idUsuario: number };
}

export interface PublicacionGuardadaRequest {
  idPublicacion: number;
  idUsuario?: number;
}