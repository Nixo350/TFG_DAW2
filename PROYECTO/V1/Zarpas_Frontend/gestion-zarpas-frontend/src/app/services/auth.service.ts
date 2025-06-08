// src/app/services/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of, BehaviorSubject } from 'rxjs'; // <-- ¡Importa BehaviorSubject!
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

  // BehaviorSubjects para el estado reactivo
  private _isLoggedIn$ = new BehaviorSubject<boolean>(false);
  private _currentUser$ = new BehaviorSubject<any | null>(null); // Guardará el objeto de usuario decodificado o de localStorage

  // Observables públicos para que otros componentes se suscriban
  isLoggedIn$ = this._isLoggedIn$.asObservable();
  currentUser$ = this._currentUser$.asObservable();

  constructor(private http: HttpClient) {
    // Inicializa el estado al cargar el servicio
    this.checkInitialLoginStatus();
  }

  // Método para verificar el estado de login inicial
  private checkInitialLoginStatus(): void {
    const token = this.getToken();
    const user = this.getUserFromLocalStorage(); // Obtén el objeto de usuario de localStorage

    if (token && user) {
      try {
        const decodedToken = this.decodeToken(token); // Usamos tu función decodeToken
        const currentTime = Date.now() / 1000;

        // Asumiendo que tu token JWT tiene un campo 'exp' (expiration time)
        if (decodedToken && decodedToken.exp < currentTime) {
          // Token expirado, limpiar y establecer a no logueado
          this.logout(false); // No redirigir de inmediato en la inicialización
        } else {
          // Token válido, establecer como logueado
          this._isLoggedIn$.next(true);
          this._currentUser$.next(user); // Emite el usuario de localStorage
        }
      } catch (e) {
        console.error('Error al decodificar o validar el token en la inicialización:', e);
        this.logout(false); // Si hay error en el token, desloguear
      }
    } else {
      // No hay token o usuario en localStorage
      this._isLoggedIn$.next(false);
      this._currentUser$.next(null);
    }
  }

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
        const userObj = {
          id: response.id,
          username: response.username,
          email: response.email,
          roles: response.roles
        };
        localStorage.setItem('auth-user', JSON.stringify(userObj));

        // Notifica a los suscriptores que el usuario ha iniciado sesión
        this._isLoggedIn$.next(true);
        this._currentUser$.next(userObj); // Emite el objeto de usuario
      }),
      // Puedes añadir un manejo de errores más sofisticado aquí si lo deseas
      catchError(this.handleError<any>('login'))
    );
  }

  getCurrentUserId(): Observable<number | null> {
    const user = this.getUserFromLocalStorage(); // Ahora obtienes el usuario del localStorage
    return of(user && user.id ? user.id : null);
  }

  /**
   * Método auxiliar para decodificar el token.
   * Asume que el payload del token es un JSON base64-encoded.
   * Devolverá el payload decodificado o null si hay un error.
   */
  private decodeToken(token: string): any {
    const parts = token.split('.');
    if (parts.length === 3) {
      try {
        const payload = JSON.parse(atob(parts[1]));
        return payload;
      } catch (e) {
        console.error('Failed to parse token payload:', e);
        return null;
      }
    }
    return null;
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
   * Notifica a los suscriptores.
   */
  logout(redirectToLogin: boolean = true): void {
    localStorage.removeItem('auth-token');
    localStorage.removeItem('auth-user');
    // Notifica a los suscriptores que el usuario ya NO está logueado
    this._isLoggedIn$.next(false);
    this._currentUser$.next(null);
    // La redirección al login se manejará en el NavbarComponent o en el componente que lo llame.
  }

  /**
   * Obtiene la información del usuario actualmente logueado desde localStorage.
   * @returns El objeto de usuario parseado o null si no hay usuario.
   */
  getUserFromLocalStorage(): any | null {
    const user = localStorage.getItem('auth-user');
    return user ? JSON.parse(user) : null;
  }

  /**
   * Verifica si el usuario está logueado comprobando la existencia y validez del token.
   * Este método es síncrono y se usa para cheques rápidos o al inicio.
   * La fuente de verdad reactiva es isLoggedIn$.
   * @returns true si el usuario está logueado y el token es válido, false en caso contrario.
   */
  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }
    try {
      const decodedToken = this.decodeToken(token);
      const currentTime = Date.now() / 1000;
      // Asegúrate de que el token decodificado exista y tenga una propiedad 'exp'
      return decodedToken && decodedToken.exp && decodedToken.exp > currentTime;
    } catch (e) {
      console.error('Error al decodificar token en isLoggedIn (síncrono):', e);
      return false;
    }
  }

  /**
   * Obtiene el token JWT del localStorage.
   * @returns El token JWT como string o null si no existe.
   */
  getToken(): string | null {
    return localStorage.getItem('auth-token');
  }

  /**
   * Manejador de errores básico para las peticiones HTTP.
   * Puedes personalizarlo para mostrar notificaciones o logs más detallados.
   */
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(`${operation} failed:`, error); // Loguea el error en la consola
      return of(error); // Devuelve el error para que el componente que llama lo maneje
    };
  }
  getUser(): any | null {
    const user = localStorage.getItem('auth-user');
    return user ? JSON.parse(user) : null;
  }
}