
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { Publicacion } from '../modelos/Publicacion'; 
import { Categoria } from '../modelos/Categoria';
import { AuthService } from './auth.service';
import { map } from 'rxjs/operators';
import { environment } from '../../enviroments/environment.prod';

interface PublicacionGuardadaRequest {
  idPublicacion: number;
  idUsuario: number;
}

const BASE_API_URL = `${environment.apiUrl}/publicaciones`;

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

  //Clase encargada de buscar las publicaciones
  searchPublicaciones(searchTerm: string): Observable<Publicacion[]> {
    return this.http.get<Publicacion[]>(`${BASE_API_URL}/buscar?keyword=${searchTerm}`);
  }



  getPublicacionesByCategoria(nombreCategoria: string): Observable<Publicacion[]> {
    return this.http.get<Publicacion[]>(`${BASE_API_URL}/categoria/${nombreCategoria}`);
  }

  getAllCategorias(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(`${BASE_API_URL}/categorias/all`);
  }

  getPublicacionesByUserId(userId: number): Observable<Publicacion[]> {
    return this.http.get<Publicacion[]>(`${BASE_API_URL}/usuario/${userId}`);
  }

  deletePublicacion(publicacionId: number): Observable<void> {
    return this.http.delete<void>(`${BASE_API_URL}/${publicacionId}`);
  }


}