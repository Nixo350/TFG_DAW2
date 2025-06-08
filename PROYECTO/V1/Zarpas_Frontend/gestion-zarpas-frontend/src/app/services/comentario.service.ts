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

  // --- ¡REVISA ESTA LÍNEA CUIDADOSAMENTE! ---
  obtenerComentariosPorPublicacion(idPublicacion: number): Observable<Comentario[]> {
    // Asegúrate de que no haya caracteres ocultos o etiquetas HTML aquí.
    // La URL esperada es: http://localhost:9000/api/comentarios/publicacion/{idPublicacion}
    return this.http.get<Comentario[]>(`${this.baseUrl}/publicacion/${idPublicacion}`);
  }

  actualizarComentario(idComentario: number, texto: string): Observable<Comentario> {
    const requestBody = { texto: texto };
    return this.http.put<Comentario>(`${this.baseUrl}/${idComentario}`, requestBody, { headers: this.getAuthHeaders() });
  }

  eliminarComentario(idComentario: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${idComentario}`, { headers: this.getAuthHeaders(), observe: 'response' })
      .pipe(
        // Puedes agregar un catchError si necesitas manejar errores específicos aquí
        // Por ejemplo, para respuestas 404 de comentarios no encontrados
        map(response => undefined) // Mapea la respuesta a void si no hay cuerpo
      );
  }
}