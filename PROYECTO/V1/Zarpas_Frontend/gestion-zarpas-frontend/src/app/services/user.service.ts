// src/app/services/user.service.ts (o la ruta correcta de tu UserService)

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs'; // Necesario para Observable

@Injectable({
  providedIn: 'root'
})
export class UserService {

  // Asegúrate de que esta URL base sea correcta para tu backend
  private baseUrl = 'http://localhost:9000/api/auth'; // <--- Base URL para autenticación

  constructor(private http: HttpClient) { }

  // Método para registrar un nuevo usuario
  registerUser(userData: any): Observable<any> {
    // <<-- ¡¡CORRIGE ESTA LÍNEA!! -->>
    // La URL debe ser `${this.baseUrl}/signup`
    return this.http.post(`${this.baseUrl}/signup`, userData); // <-- Correcto
  }

  // ... otros métodos si los tienes ...
}