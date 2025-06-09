import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of, BehaviorSubject } from 'rxjs'; 
import { tap, catchError } from 'rxjs/operators';

const AUTH_API = 'http://localhost:9000/api/auth/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private _isLoggedIn$ = new BehaviorSubject<boolean>(false);
  private _currentUser$ = new BehaviorSubject<any | null>(null); 

  isLoggedIn$ = this._isLoggedIn$.asObservable();
  currentUser$ = this._currentUser$.asObservable();

  constructor(private http: HttpClient) {
    this.checkInitialLoginStatus();
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

  logout(redirectToLogin: boolean = true): void {
    localStorage.removeItem('auth-token');
    localStorage.removeItem('auth-user');
    this._isLoggedIn$.next(false);
    this._currentUser$.next(null);
    
  }


  getUserFromLocalStorage(): any | null {
    const user = localStorage.getItem('auth-user');
    return user ? JSON.parse(user) : null;
  }


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


  getToken(): string | null {
    return localStorage.getItem('auth-token');
  }


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
}