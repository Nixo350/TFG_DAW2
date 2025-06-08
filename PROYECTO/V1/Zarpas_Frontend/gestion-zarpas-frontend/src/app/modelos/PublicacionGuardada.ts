// src/app/modelos/PublicacionGuardada.ts
import { Publicacion } from './Publicacion';
import { Usuario } from './Usuario';

export interface PublicacionGuardada {
  idPublicacionGuardada?: number; // Puede que tenga un ID propio
  publicacion?: { idPublicacion: number }; // Solo si la necesitas para la relación
  usuario?: { idUsuario: number };
}

// Interfaz para la solicitud de guardar/desguardar (si tu PublicacionGuardadaService la usa)
export interface PublicacionGuardadaRequest {
  idPublicacion: number;
  idUsuario?: number;
}