// src/app/services/comentario-reaccion.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';
import { TipoReaccion } from '../modelos/TipoReaccion';
import { ComentarioReaccionRequest } from '../modelos/ComentarioReaccion';

@Injectable({
  providedIn: 'root'
})
export class ComentarioReaccionService {
  private baseUrl = 'http://localhost:9000/api/reacciones-comentario';

  constructor(private http: HttpClient, private authService: AuthService) { }

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    if (token) {
      return new HttpHeaders({
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      });
    }
    console.warn('No authentication token available for getAuthHeaders.');
    return new HttpHeaders({ 'Content-Type': 'application/json' });
  }

  toggleReaccion(request: ComentarioReaccionRequest): Observable<HttpResponse<any>> {
    return this.http.put<any>(`${this.baseUrl}/toggle`, request, {
      headers: this.getAuthHeaders(),
      observe: 'response' // Para obtener el HttpResponse completo
    });
  }

  getConteoReacciones(idComentario: number): Observable<{ like: number, dislike: number }> {
    return this.http.get<{ like: number, dislike: number }>(`${this.baseUrl}/conteo/${idComentario}`);
  }

  getReaccionUsuario(idUsuario: number, idComentario: number): Observable<TipoReaccion | null> {
    // Si la reacción no existe, el backend devuelve 404, que rxjs/HttpClient mapea a un error.
    // Usaremos catchError en el componente para manejarlo y devolver null.
    return this.http.get<TipoReaccion>(`${this.baseUrl}/usuario/${idUsuario}/comentario/${idComentario}`, { headers: this.getAuthHeaders() });
  }
}