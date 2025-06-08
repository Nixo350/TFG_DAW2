// src/app/services/category.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';
import { Categoria } from '../modelos/Categoria'; // Importa la interfaz Category

@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  private baseUrl = 'http://localhost:9000/api/categorias'; // Nueva URL base para Categorías

  constructor(private http: HttpClient, private authService: AuthService) { }

  // Método para obtener los encabezados de autenticación
  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  /**
   * Obtiene todas las categorías del backend.
   * @returns Un Observable con un array de objetos Category.
   */
  getAllCategorias(): Observable<Categoria[]> {
    // Asumiendo que el endpoint /api/categorias/all ahora devuelve List<Categoria> (objetos completos)
    // en lugar de solo List<String>. Si tu backend sigue devolviendo List<String>,
    // tendrás que mapear aquí o ajustarlo en el backend.
    // Si tu backend en PublicacionController devuelve List<String>, y CategoryController devuelve List<Categoria>,
    // usa este. Si solo usas el de PublicacionController, ajusta el tipo de retorno aquí.
    return this.http.get<Categoria[]>(`${this.baseUrl}/all`); // No requiere autenticación si es permitido por WebSecurityConfig
  }

  /**
   * Crea una nueva categoría en el backend.
   * @param categoryName El nombre de la categoría a crear.
   * @returns Un Observable con la categoría creada.
   */
  createCategory(nombre: string, descripcion: string = ''): Observable<Categoria> {
    const requestBody = { nombre: nombre, descripcion: descripcion };
    return this.http.post<Categoria>(this.baseUrl, requestBody, { headers: this.getAuthHeaders() });
  }

  // Opcional: Métodos para actualizar y eliminar si los necesitas en alguna interfaz de administración
  updateCategory(idCategoria: number, nombre: string, descripcion: string = ''): Observable<Categoria> {
    const requestBody = { nombre: nombre, descripcion: descripcion };
    return this.http.put<Categoria>(`<span class="math-inline">\{this\.baseUrl\}/</span>{idCategoria}`, requestBody, { headers: this.getAuthHeaders() });
  }

  deleteCategory(idCategoria: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`<span class="math-inline">\{this\.baseUrl\}/</span>{idCategoria}`, {
      headers: this.getAuthHeaders(),
      observe: 'response'
    });
  }
}