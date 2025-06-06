import { Comentario } from "./Comentario";
import { Usuario } from "./Usuario";

export interface Publicacion {
    idPublicacion: number;
    usuario: Usuario;
    titulo: string;
    contenido: string;
    imagenUrl?: string;
    fechaCreacion: Date;
    fechaModificacion?: Date;
    comentarios: Comentario[];
    usuariosGuardaron: Usuario[];
  }