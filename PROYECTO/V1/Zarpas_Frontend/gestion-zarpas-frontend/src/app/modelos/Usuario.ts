import { Chat } from "./Chat";
import { Comentario } from "./Comentario";
import { ComentarioReaccion } from "./ComentarioReaccion";
import { Mensaje } from "./Mensaje";
import { Publicacion } from "./Publicacion";
import { PublicacionGuardada } from "./PublicacionGuardada";
import { UsuarioRol } from "./UsuarioRol";

export interface Usuario {
    idUsuario: number;
    username: string;
    email: string;
    nombre: string;
    contrasena: string;
    fechaRegistro: Date;
    ultimoLogin: Date;
    fotoPerfil: string;
    publicaciones: Publicacion[];
    comentarios: Comentario[];
    chats: Chat[];
    mensajesEnviados: Mensaje[];
    publicacionesGuardadas: PublicacionGuardada[];
    comentariosReaccionados: ComentarioReaccion[];
    usuarioRoles: UsuarioRol[];
  }