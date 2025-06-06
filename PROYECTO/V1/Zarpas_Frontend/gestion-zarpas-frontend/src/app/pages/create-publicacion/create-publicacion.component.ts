// src/app/pages/create-publicacion/create-publicacion.component.ts
import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service'; // <--- ¡Ahora sí, descomentamos y usamos!

// Define una interfaz para la estructura de la publicación que enviaremos
interface Publicacion {
  titulo: string;
  contenido: string;
  //imagenUrl?: string; // Opcional
  usuario: {
    idUsuario: number; // El ID del usuario que crea la publicación
  };
  fechaCreacion?: string; // No es necesario enviarlo, el backend lo genera
  fechaModificacion?: string; // No es necesario enviarlo, el backend lo genera
}

@Component({
  selector: 'app-create-publicacion',
  templateUrl: './create-publicacion.component.html',
  styleUrls: ['./create-publicacion.component.css'],
  standalone: true,
  imports: [
    FormsModule,
    HttpClientModule,
    CommonModule,
    RouterModule
  ]
})
export class CreatePublicacionComponent implements OnInit {

  publicacion: Publicacion = {
    titulo: '',
    contenido: '',
    //imagenUrl: '',
    usuario: {
      idUsuario: 0 // Se inicializará con el ID del usuario logueado
    }
  };
  selectedFile: File | null = null;

  message: string = '';
  isSuccess: boolean = false;
  private baseUrl = 'http://localhost:9000/api/publicaciones'; // Tu URL del endpoint de publicaciones

  constructor(
    private http: HttpClient,
    private router: Router,
    private authService: AuthService // <--- Inyectamos el AuthService
  ) { }

  ngOnInit(): void {
    // Obtener el ID del usuario logueado usando AuthService
    const currentUser = this.authService.getUser();
    if (currentUser && currentUser.id) { // Asegúrate de que el usuario y su ID existan
      this.publicacion.usuario.idUsuario = currentUser.id;
    } else {
      // Si no hay usuario logueado o su ID no está disponible, redirigir al login
      console.warn('Usuario no logueado o ID de usuario no disponible. Redirigiendo al login.');
      this.router.navigate(['/login']);
    }
  }

    // <--- Nuevo método: Manejar la selección de archivo
    onFileSelected(event: any): void {
      const file = event.target.files[0];
      if (file) {
        this.selectedFile = file;
        console.log('Archivo seleccionado:', file.name);
      } else {
        this.selectedFile = null;
        console.log('Ningún archivo seleccionado.');
      }
    }

  onSubmit(): void {
    this.message = 'Enviando publicación...';
    this.isSuccess = false;

    ///
    const formData = new FormData();
    formData.append('titulo', this.publicacion.titulo);
    formData.append('contenido', this.publicacion.contenido);
    formData.append('idUsuario', this.publicacion.usuario.idUsuario.toString()); // Enviar el ID del usuario

    if (this.selectedFile) {
      formData.append('file', this.selectedFile, this.selectedFile.name); // 'file' es el nombre que espera el backend
    }
    ///
    console.log('Datos a enviar:', formData); // Para depuración

    this.http.post<Publicacion>(this.baseUrl, formData).subscribe({
      next: (response) => {
        this.message = 'Publicación creada con éxito!';
        this.isSuccess = true;
        this.resetForm();
        setTimeout(() => {
          this.router.navigate(['/dashboard']);
        }, 2000);
      },
      error: (error) => {
        console.error('Error al crear la publicación:', error);
        this.message = 'Error al crear la publicación. Inténtalo de nuevo.';
        this.isSuccess = false;

        if (error.error && error.error.message) {
            this.message = `Error: ${error.error.message}`;
        } else if (error.status === 400) {
            this.message = 'Error de validación: Comprueba los campos.';
        } else if (error.status === 404) {
             this.message = 'Error: El usuario asociado no fue encontrado. Asegúrate de que el ID de usuario es válido.';
        } else {
            this.message = 'Ha ocurrido un error inesperado. Por favor, inténtalo más tarde.';
        }
      }
    });
  }

  resetForm(): void {
    this.publicacion = {
      titulo: '',
      contenido: '',
      //imagenUrl: '',
      usuario: {
        idUsuario: this.publicacion.usuario.idUsuario // Mantener el ID de usuario
      }
    };
    this.selectedFile = null; // Reiniciar el archivo seleccionado
    // Si usas un input type="file", puede que necesites resetearlo manualmente
    // (ej: usando un ViewChild o reiniciando el formulario completo si es posible)
    const fileInput = document.getElementById('imagenUpload') as HTMLInputElement;
    if (fileInput) {
      fileInput.value = '';
    }
  }
}