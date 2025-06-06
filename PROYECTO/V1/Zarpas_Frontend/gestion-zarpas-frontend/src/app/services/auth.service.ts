import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';

// URL base de tu API de autenticación en el backend
const AUTH_API = 'http://localhost:9000/api/auth/';

// Opciones HTTP para indicar que el contenido es JSON
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) { }

  /**
   * Envía las credenciales al backend para iniciar sesión.
   * Guarda el token JWT y la información del usuario en localStorage al éxito.
   */
  login(username: string, password_param: string): Observable<any> {
    return this.http.post(AUTH_API + 'signin', {
      username,
      password: password_param
    }, httpOptions).pipe(
      tap((response: any) => {
        // Almacenar el token JWT y la información del usuario en localStorage
        localStorage.setItem('auth-token', response.token);
        localStorage.setItem('auth-user', JSON.stringify({
          id: response.id,
          username: response.username,
          email: response.email,
          roles: response.roles
        }));
      }),
      // Puedes añadir un manejo de errores más sofisticado aquí si lo deseas
      catchError(this.handleError<any>('login'))
    );
  }

  /**
   * Registra un nuevo usuario enviando sus datos al backend.
   */
  registerUser(user: any): Observable<any> {
    return this.http.post(AUTH_API + 'signup', {
      username: user.username,
      email: user.email,
      contrasena: user.contrasena,
      nombre: user.nombre
    }, httpOptions);
  }

  /**
   * Cierra la sesión del usuario eliminando el token y la información del usuario de localStorage.
   */
  logout(): void {
    localStorage.removeItem('auth-token');
    localStorage.removeItem('auth-user');
  }

  /**
   * Obtiene la información del usuario actualmente logueado desde localStorage.
   * @returns El objeto de usuario parseado o null si no hay usuario.
   */
  getUser(): any | null {
    const user = localStorage.getItem('auth-user');
    return user ? JSON.parse(user) : null;
  }

  /**
   * Verifica si el usuario está logueado comprobando la existencia del token y la información del usuario.
   * @returns true si el usuario está logueado, false en caso contrario.
   */
  isLoggedIn(): boolean {
    return localStorage.getItem('auth-token') !== null && localStorage.getItem('auth-user') !== null;
  }

  /**
   * Manejador de errores básico para las peticiones HTTP.
   * Puedes personalizarlo para mostrar notificaciones o logs más detallados.
   */
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(`${operation} failed:`, error); // Loguea el error en la consola
      // Permite que la aplicación siga funcionando devolviendo un resultado vacío.
      return of(error); // Devuelve el error para que el componente que llama lo maneje
    };
  }
}