// src/app/services/post.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { Publicacion } from '../modelos/Publicacion'; // Asegúrate de importar Categoria también
import { Categoria } from '../modelos/Categoria';
import { AuthService } from './auth.service';
import { map } from 'rxjs/operators';

interface PublicacionGuardadaRequest {
  idPublicacion: number;
  idUsuario: number;
}

const BASE_API_URL = 'http://localhost:9000/api/publicaciones';

@Injectable({
  providedIn: 'root'
})
export class PostService {

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) { }

  getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('auth-token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  getPublicaciones(): Observable<Publicacion[]> {
    return this.http.get<Publicacion[]>(`${BASE_API_URL}/todas`);
  }

  searchPublicaciones(searchTerm: string): Observable<Publicacion[]> {
    return this.http.get<Publicacion[]>(`${BASE_API_URL}/buscar?keyword=${searchTerm}`);
  }

  getPublicacionById(id: number): Observable<Publicacion> {
    return this.http.get<Publicacion>(`${BASE_API_URL}/${id}`);
  }

  likePublicacion(idPublicacion: number): Observable<any> {
    return this.http.post(`${BASE_API_URL}/reacciones/like/${idPublicacion}`, {}, { headers: this.getAuthHeaders() });
  }

  dislikePublicacion(idPublicacion: number): Observable<any> {
    return this.http.post(`${BASE_API_URL}/reacciones/dislike/${idPublicacion}`, {}, { headers: this.getAuthHeaders() });
  }

  getConteoReacciones(idPublicacion: number): Observable<{ like: number; dislike: number }> {
    return this.http.get<{ like: number; dislike: number }>(`${BASE_API_URL}/reacciones/conteo/${idPublicacion}`, { headers: this.getAuthHeaders() });
  }

  getPublicacionesByCategoria(nombreCategoria: string): Observable<Publicacion[]> {
    return this.http.get<Publicacion[]>(`${BASE_API_URL}/categoria/${nombreCategoria}`);
  }

  getAllCategorias(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(`${BASE_API_URL}/categorias/all`);
  }

  crearPublicacionConImagen(formData: FormData): Observable<Publicacion> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });
    return this.http.post<Publicacion>(`${BASE_API_URL}/crear-con-imagen`, formData, { headers: headers });
  }

  // --- NUEVOS MÉTODOS PARA ACTUALIZAR PUBLICACIONES ---

  /**
   * Actualiza una publicación existente sin cambiar la imagen.
   * @param publicacion La publicación a actualizar.
   * @returns Un Observable con la Publicacion actualizada.
   */
  actualizarPublicacion(publicacion: Publicacion): Observable<Publicacion> {
    // Asegúrate de que el ID de la publicación esté presente para la actualización
    if (!publicacion.idPublicacion) {
      // Manejar el error o lanzar una excepción si el ID no está presente
      return new Observable<Publicacion>(observer => {
        observer.error(new Error('El ID de la publicación es necesario para actualizar.'));
      });
    }
    // No Content-Type header aquí si envías JSON directamente con HttpClient,
    // pero si getAuthHeaders ya lo incluye, está bien.
    return this.http.put<Publicacion>(
      `${BASE_API_URL}/${publicacion.idPublicacion}`, // Endpoint para actualizar por ID
      publicacion,
      { headers: this.getAuthHeaders() }
    );
  }

  /**
   * Actualiza una publicación existente, incluyendo el cambio de imagen.
   * @param idPublicacion El ID de la publicación a actualizar.
   * @param formData FormData que contiene los datos de la publicación y la nueva imagen.
   * @returns Un Observable con la Publicacion actualizada.
   */
  actualizarPublicacionConImagen(idPublicacion: number, formData: FormData): Observable<Publicacion> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });
    // El backend debe esperar un PUT en el endpoint '/{id}/actualizar-con-imagen' o similar
    return this.http.put<Publicacion>(
      `${BASE_API_URL}/${idPublicacion}/actualizar-con-imagen`,
      formData,
      { headers: headers }
    );
  }
  updatePublicacion(idPublicacion: number, publicacion: Publicacion): Observable<Publicacion> {
    // Asegúrate de que tu endpoint de backend para actualizar publicaciones sea /api/publicaciones/{id}
    return this.http.put<Publicacion>(`${BASE_API_URL}/${idPublicacion}`, publicacion, { headers: this.getAuthHeaders() });
  }

  // --- FIN NUEVOS MÉTODOS ---

  createCategory(categoria: Categoria): Observable<Categoria> {
    return this.http.post<Categoria>(`${BASE_API_URL}/categorias`, categoria, { headers: this.getAuthHeaders() });
  }
    // Método para eliminar una publicación
    deletePublicacion(idPublicacion: number): Observable<any> {
      return this.http.delete(`${BASE_API_URL}/${idPublicacion}`, { headers: this.getAuthHeaders() });
    }
    savePublicacion(request: PublicacionGuardadaRequest): Observable<any> {
      // Ejemplo de endpoint para guardar
      return this.http.post(`${BASE_API_URL}/guardar`, request, { headers: this.getAuthHeaders() });
    }
  
    unsavePublicacion(request: PublicacionGuardadaRequest): Observable<any> {
      // Ejemplo de endpoint para desguardar
      // Podría ser un POST, PUT o DELETE dependiendo de tu API REST
      return this.http.post(`${BASE_API_URL}/desguardar`, request, { headers: this.getAuthHeaders() });
    }
    obtenerPublicacionPorId(idPublicacion: number): Observable<Publicacion> {
      // El backend ya tiene un endpoint para esto: GET /api/publicaciones/{id}
      return this.http.get<Publicacion>(`${BASE_API_URL}/${idPublicacion}`, { headers: this.getAuthHeaders() });
    }
}