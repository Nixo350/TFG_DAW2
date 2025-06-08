import { Comentario } from "./Comentario";
import { Usuario } from "./Usuario";
import { TipoReaccion } from "./TipoReaccion";
import { ReaccionPublicacion } from "./ReaccionPublicacion";
import { Categoria } from "./Categoria";
import { PublicacionGuardada } from './PublicacionGuardada';

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
    categoria?: Categoria | null;

    mostrarComentarios?: boolean; 
    guardadosPorUsuarios?: PublicacionGuardada[]; 
    isSaved?: boolean; 
  }