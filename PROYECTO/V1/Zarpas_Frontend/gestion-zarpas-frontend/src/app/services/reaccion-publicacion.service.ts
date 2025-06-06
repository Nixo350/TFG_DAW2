import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
// import { ReaccionPublicacion } from '../modelos/ReaccionPublicacion'; // No es estrictamente necesario si solo envías el DTO
import { TipoReaccion } from '../modelos/TipoReaccion';
import { AuthService } from './auth.service';


@Injectable({
  providedIn: 'root'
})
export class ReaccionPublicacionService {
  private baseUrl = 'http://localhost:9000/api/reacciones-publicacion'; // Base URL de tu controlador

  constructor(private http: HttpClient, private authService: AuthService) { }

  /**
   * Envía una petición PUT para crear o actualizar una reacción a una publicación.
   * El cuerpo de la petición coincide con el DTO ReaccionPublicacionRequest del backend.
   * @param idUsuario El ID del usuario que realiza la reacción.
   * @param idPublicacion El ID de la publicación a la que se reacciona.
   * @param tipoReaccion El tipo de reacción (LIKE, DISLIKE).
   */
  toggleReaccion(idUsuario: number, idPublicacion: number, tipoReaccion: TipoReaccion): Observable<any> {
    const token = this.authService.getToken(); // Obtiene el token de autenticación
    if (!token) {
      console.error('No hay token de autenticación disponible.');
      throw new Error('No authentication token available.');
    }

    // Los headers ya incluyen 'Content-Type': 'application/json' por defecto en el httpOptions
    // pero lo especificamos aquí por claridad y para el Authorization header
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    // El cuerpo de la petición debe coincidir con ReaccionPublicacionRequest en el backend
    const body = {
      idUsuario: idUsuario,
      idPublicacion: idPublicacion,
      tipoReaccion: tipoReaccion
    };

    // Realiza la petición PUT al URL base del controlador
    return this.http.put<any>(`${this.baseUrl}/toggle`, body, {
      headers: this.getAuthHeaders(),
      observe: 'response' // <-- ¡CLAVE! Esto hace que devuelva el objeto HttpResponse completo
    });
    
  }



   /**
   * Obtiene el conteo de likes y dislikes para una publicación específica.
   * Llama a GET /api/reacciones-publicacion/conteo/{idPublicacion}
   * @param idPublicacion El ID de la publicación.
   * @returns Un Observable que emite un objeto como { like: 5, dislike: 2 }.
   */
   getConteoReacciones(idPublicacion: number): Observable<{ like: number, dislike: number }> {
    return this.http.get<{ like: number, dislike: number }>(`${this.baseUrl}/conteo/${idPublicacion}`);
  }


  /**
   * Obtiene el tipo de reacción del usuario actual para una publicación.
   * Llama a GET /api/reacciones-publicacion/usuario/{idUsuario}/publicacion/{idPublicacion}
   * @param idUsuario El ID del usuario.
   * @param idPublicacion El ID de la publicación.
   * @returns Un Observable que emite 'like', 'dislike' o lanza un error 404 si no hay reacción.
   */
  getReaccionUsuario(idUsuario: number, idPublicacion: number): Observable<TipoReaccion | null> {
    const token = this.authService.getToken();
    if (!token) {
      console.warn('No hay token de autenticación disponible para getReaccionUsuario.');
      // Si no hay token, no podemos obtener la reacción del usuario, devuelve null
      return new Observable(observer => {
        observer.next(null);
        observer.complete();
      });
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    // HttpClient maneja 404 automáticamente como un error, que se captura en catchError en dashboard.component.ts
    return this.http.get<TipoReaccion>(`${this.baseUrl}/usuario/${idUsuario}/publicacion/${idPublicacion}`, { headers: headers });
  }
  // Método auxiliar para obtener las opciones HTTP con el token de autorización
  // Centraliza la obtención del token usando el AuthService
  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken(); // Obtén el token usando el servicio de autenticación
    if (token) {
      return new HttpHeaders({
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      });
    }
    // Si no hay token, devuelve solo el Content-Type o maneja el error apropiadamente
    console.warn('No authentication token available for getAuthHeaders.');
    return new HttpHeaders({ 'Content-Type': 'application/json' });
  }

}