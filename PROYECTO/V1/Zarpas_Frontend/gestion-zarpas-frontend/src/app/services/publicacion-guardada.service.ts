
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PublicacionGuardadaRequest } from '../modelos/PublicacionGuardada'; 
import { environment } from '../../enviroments/environment.prod';

@Injectable({
  providedIn: 'root'
})
export class PublicacionGuardadaService {
  private apiUrl = `${environment.apiUrl}/publicaciones`;

  constructor(private http: HttpClient) { }

  savePublicacion(idPublicacion: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${idPublicacion}/save`, {});
  }

  unsavePublicacion(idPublicacion: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${idPublicacion}/unsave`);
  }

  getPublicacionesGuardadasPorUsuario(idUsuario: number): Observable<any[]> {

    return this.http.get<any[]>(`${this.apiUrl}/usuario/${idUsuario}/guardadas`);
  }
}