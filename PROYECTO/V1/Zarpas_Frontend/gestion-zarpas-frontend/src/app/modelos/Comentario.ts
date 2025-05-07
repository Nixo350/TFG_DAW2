import { Publicacion } from "./Publicacion";
import { Usuario } from "./Usuario";

export interface Comentario {
    idComentario: number;
    usuario: Usuario;
    publicacion: Publicacion;
    texto: string;
    fechaCreacion: Date;
    fechaModificacion: Date;
    usuariosReaccionaron: Usuario[];
  }