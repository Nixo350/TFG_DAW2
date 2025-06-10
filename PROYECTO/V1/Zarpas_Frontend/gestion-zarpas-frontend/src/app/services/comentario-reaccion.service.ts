import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs'; 
import { map, catchError } from 'rxjs/operators'; 
import { AuthService } from './auth.service';
import { TipoReaccion } from '../modelos/TipoReaccion';
import { ComentarioReaccionRequest } from '../modelos/ComentarioReaccion';
import { environment } from '../../enviroments/environment.prod';

@Injectable({
  providedIn: 'root'
})
export class ComentarioReaccionService {
  
  private baseUrl = `${environment.apiUrl}/reacciones-comentario`;

  constructor(private http: HttpClient, private authService: AuthService) { }

  getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken(); // Usa el método getToken() de AuthService
    if (token) {
      return new HttpHeaders({
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      });
    }
    console.warn('No authentication token available for getAuthHeaders.');
    return new HttpHeaders({ 'Content-Type': 'application/json' }); // Devuelve sin token si no hay
  }

  getConteoReacciones(idComentario: number): Observable<{ like: number, dislike: number }> {
    return this.http.get<{ like: number, dislike: number }>(`${this.baseUrl}/conteo/${idComentario}`);
  }
//Clase encargada mostrar reacciones tanto de comentarios y usuarios
  getReaccionByComentarioAndUsuario(idComentario: number, idUsuario: number): Observable<TipoReaccion | null> {
    return this.http.get<TipoReaccion | null>(`${this.baseUrl}/comentario/${idComentario}/usuario/${idUsuario}`, { headers: this.getAuthHeaders() });
  }

  reaccionar(request: ComentarioReaccionRequest): Observable<any> {
    console.log(request)
    return this.http.put(`${this.baseUrl}/toggle`, request);
  }
}