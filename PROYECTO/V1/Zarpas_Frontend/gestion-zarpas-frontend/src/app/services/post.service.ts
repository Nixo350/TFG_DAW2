// src/app/services/post.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { Publicacion } from '../modelos/Publicacion';
import { AuthService } from './auth.service';
import { map } from 'rxjs/operators';
import { Categoria } from '../modelos/Categoria';

const BASE_API_URL = 'http://localhost:9000/api/publicaciones'; // Asegúrate de que esta URL sea la base correcta

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

  /**
   * Obtiene todas las publicaciones desde el backend.
   * @returns Un Observable con un array de Publicacion.
   */
  getPublicaciones(): Observable<Publicacion[]> {
    return this.http.get<Publicacion[]>(`${BASE_API_URL}/todas`);
  }


  /**
   * Obtiene publicaciones filtradas por un término de búsqueda desde el backend.
   * @param searchTerm El término a buscar en los títulos, contenido o username.
   * @returns Un Observable con un array de Publicacion.
   */
  searchPublicaciones(searchTerm: string): Observable<Publicacion[]> {
    if (!searchTerm || searchTerm.trim() === '') {
      return this.getPublicaciones(); // Si el término está vacío, devuelve todas las publicaciones
    }
    return this.http.get<Publicacion[]>(`${BASE_API_URL}/search?query=${searchTerm}`, { headers: this.getAuthHeaders() });
  }

  /**
   * Obtiene el conteo de reacciones para una publicación específica.
   * @param idPublicacion El ID de la publicación.
   * @returns Un Observable con un objeto que contiene el conteo de likes y dislikes.
   */
  getConteoReacciones(idPublicacion: number): Observable<{ like: number; dislike: number }> {
    // ¡AÑADE EL ENCABEZADO DE AUTORIZACIÓN AQUÍ!
    return this.http.get<{ like: number; dislike: number }>(`${BASE_API_URL}/reacciones/conteo/${idPublicacion}`, { headers: this.getAuthHeaders() });
  }

  // Otros métodos de tu servicio de publicaciones si los tienes
  // Por ejemplo, para crear publicaciones, etc.

    // --- ¡NUEVOS MÉTODOS PARA CATEGORÍAS! ---
    getPublicacionesByCategoria(nombreCategoria: string): Observable<Publicacion[]> {
      return this.http.get<Publicacion[]>(`${BASE_API_URL}/categoria/${nombreCategoria}`);
    }
  
    getAllCategorias(): Observable<Categoria[]> { // Cambiado a Categoria[]
      return this.http.get<Categoria[]>(`${BASE_API_URL}/categorias/all`); // <-- ¡QUITADOS LOS HEADERS!
    }
    crearPublicacionConImagen(formData: FormData): Observable<Publicacion> {
      // No Content-Type header aquí, el navegador lo establecerá automáticamente para FormData con el boundary correcto
      const headers = new HttpHeaders({
        'Authorization': `Bearer ${this.authService.getToken()}` // Asumiendo que getToken() existe en AuthService
      });
      return this.http.post<Publicacion>(`${BASE_API_URL}/crear-con-imagen`, formData, { headers:  this.getAuthHeaders() });
    }
  // --- ¡NUEVO MÉTODO PARA CREAR CATEGORÍA! ---
  createCategory(categoria: Categoria): Observable<Categoria> {
    // Para crear una categoría, SÍ necesitas autenticación, por eso se envían los headers
    return this.http.post<Categoria>(`${BASE_API_URL}/categorias`, categoria, { headers: this.getAuthHeaders() });
  }

    
}