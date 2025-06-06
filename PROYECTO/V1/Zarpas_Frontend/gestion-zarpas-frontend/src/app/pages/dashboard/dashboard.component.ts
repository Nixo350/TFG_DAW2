// src/app/pages/dashboard/dashboard.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; // Para directivas como *ngFor, *ngIf y pipes
import { MatButtonModule } from '@angular/material/button'; // Si usas botones de Material
import { PostService } from '../../services/post.service'; // Importa el servicio
import { Publicacion } from '../../modelos/Publicacion'; // Importa el modelo
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-dashboard',
  standalone: true, // Asegúrate de que sea standalone si lo creaste así
  imports: [
    CommonModule,
    MatButtonModule,
    HttpClientModule 
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {

  posts: Publicacion[] = []; // Array donde se almacenarán las publicaciones

  constructor(private postService: PostService) { } // Inyecta el PostService

  ngOnInit(): void {
    this.getPosts(); // Llama al método para obtener las publicaciones cuando el componente se inicialice
  }

  getPosts(): void {
    this.postService.getPublicaciones().subscribe({
      next: (data) => {
        // Mapea las fechas de string (JSON de Timestamp) a objetos Date
        this.posts = data.map(post => ({
          ...post,
          fechaCreacion: new Date(post.fechaCreacion),
          // Solo convierte fechaModificacion si existe, ya que puede ser opcional
          fechaModificacion: post.fechaModificacion ? new Date(post.fechaModificacion) : undefined
        }));
        console.log('Publicaciones cargadas:', this.posts);
      },
      error: (error) => {
        console.error('Error al cargar publicaciones:', error);
        // Aquí puedes manejar el error, por ejemplo, mostrando un mensaje al usuario
      }
    });
  }
}