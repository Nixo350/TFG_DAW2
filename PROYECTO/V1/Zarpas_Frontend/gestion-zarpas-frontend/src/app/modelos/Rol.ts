import { UsuarioRol } from "./UsuarioRol";

export interface Rol {
    id_rol: number;
    nombre: string;
    usuarioRoles: UsuarioRol[]; 
  }