import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of, BehaviorSubject,map } from 'rxjs'; 
import { tap, catchError } from 'rxjs/operators';
import { environment } from '../../enviroments/environment.prod';


interface User {
  id: number;
  username: string;
  email: string;
  fotoPerfil?: string; // Asegúrate de que el campo exista y sea opcional si no siempre está presente
  // Agrega aquí otros campos que tu objeto de usuario pueda tener
}


const AUTH_API = `${environment.apiUrl}/auth/`;

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};


@Injectable({
  providedIn: 'root'
})
export class AuthService {

  //Clase encargada del la autentificacion del usuario con un TOKEN
  private currentUserSubject: BehaviorSubject<User | null>;
  private _isLoggedIn$ = new BehaviorSubject<boolean>(false);
  private _currentUser$ = new BehaviorSubject<any | null>(null); 

  isLoggedIn$ = this._isLoggedIn$.asObservable();
  currentUser$ = this._currentUser$.asObservable();

  constructor(private http: HttpClient) {
    const storedUser = localStorage.getItem('currentUser');
    this.currentUserSubject = new BehaviorSubject<User | null>(storedUser ? JSON.parse(storedUser) : null);
    this.checkInitialLoginStatus();
  }

  public get currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  private checkInitialLoginStatus(): void {
    const token = this.getToken();
    const user = this.getUserFromLocalStorage();

    if (token && user) {
      try {
        const decodedToken = this.decodeToken(token);
        const currentTime = Date.now() / 1000;

        if (decodedToken && decodedToken.exp < currentTime) {
          this.logout(false); 
        } else {
 
          this._isLoggedIn$.next(true);
          this._currentUser$.next(user); 
        }
      } catch (e) {
        console.error('Error al decodificar o validar el token en la inicialización:', e);
        this.logout(false); 
      }
    } else {
      this._isLoggedIn$.next(false);
      this._currentUser$.next(null);
    }
  }

  public saveUserToLocalStorage(user: any): void {
    localStorage.setItem('auth-user', JSON.stringify(user));
    this._currentUser$.next(user); 
  }

//Clase encargada de el login del usuario
  login(username: string, password_param: string): Observable<any> {
    return this.http.post(AUTH_API + 'signin', {
      username,
      password: password_param
    }, httpOptions).pipe(
      tap((response: any) => {
        localStorage.setItem('auth-token', response.token);
        const userObj = {
          id: response.id,
          username: response.username,
          email: response.email,
          roles: response.roles
        };
        localStorage.setItem('auth-user', JSON.stringify(userObj));

        this._isLoggedIn$.next(true);
        this._currentUser$.next(userObj); 
      }),
      catchError(this.handleError<any>('login'))
    );
  }
//Clase encargada de la decodificacion del token enviado para la autenticacion
  private decodeToken(token: string): any {
    const parts = token.split('.');
    if (parts.length === 3) {
      try {
        const payload = JSON.parse(atob(parts[1]));
        return payload;
      } catch (e) {
        console.error('Fallido de docidificar token', e);
        return null;
      }
    }
    return null;
  }
//Clase encargada para el cierre de sesion
  logout(redirectToLogin: boolean = true): void {
    localStorage.removeItem('auth-token');
    localStorage.removeItem('auth-user');
    this._isLoggedIn$.next(false);
    this._currentUser$.next(null);
    
  }

//Clase encargada de mostrar los datos del Usuario cargados por el token
  getUserFromLocalStorage(): any | null {
    const user = localStorage.getItem('auth-user');
    return user ? JSON.parse(user) : null;
  }

//Clase encargada para mostrar si el usuario de ha autenticado
  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }
    try {
      const decodedToken = this.decodeToken(token);
      const currentTime = Date.now() / 1000;
      return decodedToken && decodedToken.exp && decodedToken.exp > currentTime;
    } catch (e) {
      console.error('Error al decodificar token en isLoggedIn (síncrono):', e);
      return false;
    }
  }

//Clase encargada de retornar el token
  getToken(): string | null {
    return localStorage.getItem('auth-token');
  }

//Clase encargada de los errores en estas operaciones
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(`${operation} failed:`, error); 
      return of(error); 
    };
  }
  getUser(): any | null {
    const user = localStorage.getItem('auth-user');
    return user ? JSON.parse(user) : null;
  }

  registerUser(userData: any): Observable<any> {

    return this.http.post(`${AUTH_API}signup`, userData); 
  }
  updateCurrentUser(updatedUserData: any): void {
    // Obtén el valor actual del usuario
    const currentUser = this.currentUserSubject.value;

    if (currentUser) {
      // Combina los datos actuales con los nuevos datos proporcionados
      const newUser = { ...currentUser, ...updatedUserData };
      // Actualiza el almacenamiento local
      localStorage.setItem('currentUser', JSON.stringify(newUser));
      // Emite el usuario actualizado a todos los suscriptores
      this.currentUserSubject.next(newUser);
    }
  }
}