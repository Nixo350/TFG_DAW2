import { Mensaje } from "./Mensaje";
import { Usuario } from "./Usuario";

export interface Chat {
    idChat: number;
    nombre: string;
    fechaCreacion: Date;
    fechaModificacion: Date;
    usuarios: Usuario[];
    mensajes: Mensaje[];
  }