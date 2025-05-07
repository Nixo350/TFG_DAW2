import { Publicacion } from "./Publicacion";
import { Usuario } from "./Usuario";

export interface PublicacionGuardada {
    idUsuario: number;
    idPublicacion: number;
    fechaGuardado: Date;
    usuario: Usuario;
    publicacion: Publicacion;
  }
  
  // Opcional, si necesitas la clase Id por separado
  export interface PublicacionGuardadaId {
    idUsuario: number;
    idPublicacion: number;
  }