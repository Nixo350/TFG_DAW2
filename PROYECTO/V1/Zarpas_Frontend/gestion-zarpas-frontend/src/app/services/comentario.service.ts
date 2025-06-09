// src/app/services/comentario.service.ts

import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';
import { Comentario, ComentarioRequest } from '../modelos/Comentario';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ComentarioService {
  private baseUrl = 'http://localhost:9000/api/comentarios'; // ASEGÚRATE DE QUE ESTA BASE URL SEA CORRECTA

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

  crearComentario(request: ComentarioRequest): Observable<Comentario> {
    return this.http.post<Comentario>(this.baseUrl, request, { headers: this.getAuthHeaders() });
  }

  obtenerComentariosPorPublicacion(idPublicacion: number): Observable<Comentario[]> {

    return this.http.get<Comentario[]>(`${this.baseUrl}/publicacion/${idPublicacion}`);
  }




}