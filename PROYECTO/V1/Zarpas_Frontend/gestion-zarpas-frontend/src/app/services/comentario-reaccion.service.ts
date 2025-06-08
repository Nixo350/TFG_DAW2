// src/app/services/comentario-reaccion.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs'; // Importa 'of' para errores controlados
import { map, catchError } from 'rxjs/operators'; // Importa 'map' y 'catchError'
import { AuthService } from './auth.service';
import { TipoReaccion } from '../modelos/TipoReaccion';
import { ComentarioReaccionRequest } from '../modelos/ComentarioReaccion';

@Injectable({
  providedIn: 'root'
})
export class ComentarioReaccionService {
  private baseUrl = 'http://localhost:9000/api/reacciones-comentario';

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

  toggleReaccion(request: ComentarioReaccionRequest): Observable<HttpResponse<any>> {
    return this.http.put<any>(`${this.baseUrl}/toggle`, request, {
      headers: this.getAuthHeaders(),
      observe: 'response' // Para obtener el HttpResponse completo
    });
  }

  // --- ¡REVISA ESTA LÍNEA CUIDADOSAMENTE! ---
  getConteoReacciones(idComentario: number): Observable<{ like: number, dislike: number }> {
    // Asegúrate de que no haya caracteres ocultos o etiquetas HTML aquí.
    // La URL esperada es: http://localhost:9000/api/reacciones-comentario/conteo/{idComentario}
    return this.http.get<{ like: number, dislike: number }>(`${this.baseUrl}/conteo/${idComentario}`);
  }

  getReaccionByComentarioAndUsuario(idComentario: number, idUsuario: number): Observable<TipoReaccion | null> {
    return this.http.get<TipoReaccion | null>(`${this.baseUrl}/comentario/${idComentario}/usuario/${idUsuario}`, { headers: this.getAuthHeaders() });
  }


  getReaccionUsuario(idUsuario: number, idComentario: number): Observable<TipoReaccion | null> {
    const headers = this.getAuthHeaders(); // Usar getAuthHeaders para incluir el token

    // Si la reacción no existe, el backend devuelve 404. HttpClient mapea 404 a un error.
    // Usamos catchError para interceptar el 404 y devolver 'null'.
    return this.http.get<TipoReaccion>(`${this.baseUrl}/usuario/${idUsuario}/comentario/${idComentario}`, { headers: headers })
      .pipe(
        map(response => response), // Si hay respuesta, la mapea directamente
        catchError(error => {
          if (error.status === 404) {
            return of(null); // Si es 404, devuelve null (no hay reacción)
          }
          // Para otros errores, relanza el error
          console.error('Error al obtener reacción del usuario:', error);
          throw error;
        })
      );
  }

  reaccionar(request: ComentarioReaccionRequest): Observable<any> {
    console.log(request)
    return this.http.put(`${this.baseUrl}/toggle`, request);
  }
}