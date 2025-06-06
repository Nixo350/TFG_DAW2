// src/app/services/post.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Publicacion } from '../modelos/Publicacion'; // Importa el modelo de Publicacion
import { AuthService } from './auth.service'; // Asume que ya tienes un AuthService para JWT

// URL base de tu API de publicaciones en el backend
// ¡Asegúrate de que esta URL coincida exactamente con tu endpoint de backend!
const API_URL = 'http://localhost:9000/api/publicaciones/todas/'; // <--- ¡Esta URL debe ser la correcta!

@Injectable({
  providedIn: 'root'
})
export class PostService {

  constructor(
    private http: HttpClient,
    private authService: AuthService // Para obtener el token JWT
  ) { }

  // Método para obtener los headers de autorización con el token JWT
  getAuthHeaders(): HttpHeaders {
    // Asume que authService.getToken() o localStorage.getItem('auth-token')
    // te da el token JWT de usuario logueado.
    const token = localStorage.getItem('auth-token'); // O tu método para obtener el token
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
    // Realiza la petición GET incluyendo las cabeceras de autorización
    return this.http.get<Publicacion[]>(API_URL, { headers: this.getAuthHeaders() });
  }

  // Puedes añadir otros métodos aquí (ej. para crear, actualizar, eliminar publicaciones)
}