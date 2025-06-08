// src/app/services/reaccion-publicacion.service.ts

import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import { TipoReaccion } from '../modelos/TipoReaccion';
import { AuthService } from './auth.service';
import { catchError, of, throwError } from 'rxjs'; // Asegúrate de importar estos operadores

@Injectable({
  providedIn: 'root'
})
export class ReaccionPublicacionService {
  private baseUrl = 'http://localhost:9000/api/reacciones-publicacion';

  constructor(private http: HttpClient, private authService: AuthService) { }

  toggleReaccion(idUsuario: number, idPublicacion: number, tipoReaccion: TipoReaccion | null): Observable<HttpResponse<any>> {
    const token = this.authService.getToken();
    if (!token) {
      console.error('No hay token de autenticación disponible.');
      throw new Error('No authentication token available.');
    }

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });

    const requestBody = {
      idUsuario: idUsuario,
      idPublicacion: idPublicacion,
      tipoReaccion: tipoReaccion
    };

    return this.http.put<any>(`${this.baseUrl}/toggle`, requestBody, {
      headers: headers,
      observe: 'response' // Esta línea es fundamental
    });
  }

  // ... (Mantén el resto de tus métodos aquí)
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

  getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('auth-token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }
}