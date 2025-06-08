// src/app/services/publicacion-guardada.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PublicacionGuardadaRequest } from '../modelos/PublicacionGuardada'; // Importa la interfaz de la Request

@Injectable({
  providedIn: 'root'
})
export class PublicacionGuardadaService {
  private apiUrl = 'http://localhost:9000/api/publicaciones'; // Ajusta la URL de tu API según tu backend

  constructor(private http: HttpClient) { }

  // CAMBIO CRÍTICO AQUÍ: EL SERVICIO ESPERA UN 'number'
  savePublicacion(idPublicacion: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${idPublicacion}/save`, {});
  }

  // CAMBIO CRÍTICO AQUÍ: EL SERVICIO ESPERA UN 'number'
  unsavePublicacion(idPublicacion: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${idPublicacion}/unsave`);
  }

  // Si tienes un método para obtener las publicaciones guardadas por un usuario:
  getPublicacionesGuardadasPorUsuario(idUsuario: number): Observable<any[]> {
    // Asumiendo que el backend tiene un endpoint para esto, por ejemplo:
    // @GetMapping("/usuario/{idUsuario}/guardadas") en PublicacionController
    return this.http.get<any[]>(`${this.apiUrl}/usuario/${idUsuario}/guardadas`);
  }
}