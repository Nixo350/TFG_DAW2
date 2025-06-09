import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import { TipoReaccion } from '../modelos/TipoReaccion';
import { AuthService } from './auth.service';
import { catchError, of, throwError } from 'rxjs'; 
import { ReaccionPublicacionRequest } from '../modelos/ReaccionPublicacion';

@Injectable({
  providedIn: 'root'
})
export class ReaccionPublicacionService {
  private baseUrl = 'http://localhost:9000/api/reacciones-publicacion';

  constructor(private http: HttpClient, private authService: AuthService) { }

  getConteoReacciones(idPublicacion: number): Observable<{ like: number, dislike: number }> {
    return this.http.get<{ like: number, dislike: number }>(`${this.baseUrl}/conteo/${idPublicacion}`);
  }


  getReaccionUsuario(idUsuario: number, idPublicacion: number): Observable<TipoReaccion | null> {
    const token = this.authService.getToken();
    if (!token) {
      console.warn('No hay token de autenticación disponible para getReaccionUsuario.');
      return new Observable(observer => {
        observer.next(null);
        observer.complete();
      });
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.get<TipoReaccion>(`${this.baseUrl}/usuario/${idUsuario}/publicacion/${idPublicacion}`, { headers: headers }).pipe(
      catchError(error => {
        if (error.status === 404) {
          return of(null);
        }
        return throwError(() => error);
      })
    );
  }

  //Clase encargada para dar LIKE O DISLIKE
  reaccionar(request: ReaccionPublicacionRequest): Observable<any> {
    return this.http.put(`${this.baseUrl}/toggle`, request);
  }

  getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('auth-token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }
}