import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

const API_URL = 'http://localhost:9000/api/usuarios/';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor(private http: HttpClient, private authService: AuthService) { }

  getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    let headers = new HttpHeaders();
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }
    return headers.set('Content-Type', 'application/json');
  }

  getCurrentUserId(): number | null {
    const user = this.authService.getUser();
    return user ? user.id : null;
  }

  getUserById(id: number): Observable<any> {
    return this.http.get(API_URL + id, { headers: this.getAuthHeaders() });
  }

  updateUserProfile(id: number, data: { username: string, fotoPerfil?: string }): Observable<any> {
    return this.http.put(API_URL + id + '/perfil', data, { headers: this.getAuthHeaders() });
  }

  changeUserContrasena(id: number, newContrasena: string): Observable<string> {
    return this.http.put<string>(
      API_URL + id + '/cambiar-contrasena',
      { newContrasena },
      {
        headers: this.getAuthHeaders().delete('Content-Type'),
        responseType: 'text' as 'json'
      }
    );
  }

  deleteUser(id: number): Observable<any> {
    return this.http.delete(API_URL + id, { headers: this.getAuthHeaders() });
  }

  updateUserProfileWithImage(id: number, username: string, file: File): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('username', username);
    formData.append('file', file, file.name);

    return this.http.put(API_URL + id + '/perfil/imagen', formData, {
      headers: this.getAuthHeaders().delete('Content-Type')
    });
  }
}