import { Comentario } from "./Comentario";
import { Usuario } from "./Usuario";
import { TipoReaccion } from "./TipoReaccion";
import { ReaccionPublicacion } from "./ReaccionPublicacion";

export interface Categoria {
  idCategoria: number;
  nombre: string;
}

export interface Publicacion {
    idPublicacion: number;
    usuario: Usuario;
    titulo: string;
    contenido: string;
    imagenUrl?: string | null;
    fechaCreacion: Date | null;
  fechaModificacion?: Date | null;
    comentarios?: Comentario[];
    usuariosGuardaron?: Usuario[];
    reaccionesPublicacion?: ReaccionPublicacion[];
    conteoLikes?: number;
    conteoDislikes?: number;
    miReaccion?: TipoReaccion | null;
    categorias?: string[]; 
  }