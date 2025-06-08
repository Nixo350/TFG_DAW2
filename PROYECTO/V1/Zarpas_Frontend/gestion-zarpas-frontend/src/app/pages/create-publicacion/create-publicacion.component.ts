import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgForm, FormsModule } from '@angular/forms'; // <<-- ¡AHORA SÍ, IMPORTA FormsModule AQUÍ!
import { CommonModule } from '@angular/common'; // También podrías necesitar CommonModule
import { AuthService } from '../../services/auth.service'; // Asegúrate de que esta ruta sea correcta

// Define la interfaz para tu objeto de publicación (ajusta si tienes más campos)
interface Publicacion {
  titulo: string;
  contenido: string;
  // Otros campos si los tienes en tu modelo
}

// Define la interfaz para tu objeto de categoría (ajusta según tu backend)
interface Categoria {
  idCategoria: number;
  nombre: string;
  descripcion?: string; // Opcional
}

@Component({
  selector: 'app-create-publicacion',
  templateUrl: './create-publicacion.component.html',
  styleUrls: ['./create-publicacion.component.css'],
  // **AQUÍ ES DONDE IMPORTAS LOS MÓDULOS EN UN COMPONENTE STANDALONE**
  standalone: true, // Asegúrate de que tu componente es standalone
  imports: [
    FormsModule, // <--- ¡Importa FormsModule aquí!
    CommonModule // <--- Importa CommonModule para directivas como *ngIf, *ngFor, etc.
  ]
})
export class CreatePublicacionComponent implements OnInit {
  publicacion: Publicacion = { titulo: '', contenido: '' }; // Modelo para la publicación

  selectedImage: File | null = null; // Para almacenar el archivo de imagen seleccionado

  // Variables para la creación de categoría
  createNewCategory: boolean = false;
  selectedCategoryId: number | undefined = undefined;
  newCategoryName: string = '';
  newCategoryDescription: string = '';

  categories: Categoria[] = []; // Para almacenar las categorías existentes

  isSuccess: boolean | null = null; // Para mensajes de éxito/error en la UI
  message: string = ''; // El mensaje a mostrar

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadCategories(); // Carga las categorías existentes al iniciar el componente
  }

  loadCategories(): void {
    const token = this.authService.getToken();
    console.log(token);
    if (!token) {
      console.warn('No hay token de autenticación para cargar categorías. Es posible que no se carguen todas o que falle la petición.');
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.get<Categoria[]>('http://localhost:9000/api/publicaciones/categorias/all', { headers })
      .subscribe({
        next: (data) => {
          this.categories = data;
          console.log('Categorías cargadas:', data);
        },
        error: (error) => {
          console.error('Error al cargar las categorías:', error);
          this.message = 'Error al cargar las categorías.';
          this.isSuccess = false;
        }
      });
  }

  onFileSelected(event: any): void {
    if (event.target.files && event.target.files.length > 0) {
      this.selectedImage = event.target.files[0];
    } else {
      this.selectedImage = null;
    }
  }

  onSubmit(form: NgForm): void {
    this.isSuccess = null;
    this.message = '';

    if (form.invalid || (!this.selectedCategoryId && !this.createNewCategory) || (this.createNewCategory && !this.newCategoryName)) {
      this.message = 'Por favor, completa todos los campos requeridos y selecciona/crea una categoría.';
      this.isSuccess = false;
      return;
    }

    if (!this.selectedImage) {
      this.message = 'Por favor, selecciona una imagen para la publicación.';
      this.isSuccess = false;
      return;
    }

    const formData = new FormData();
    formData.append('titulo', this.publicacion.titulo);
    formData.append('contenido', this.publicacion.contenido);

    if (this.selectedImage) {
      formData.append('imagen', this.selectedImage, this.selectedImage.name);
    }

    if (this.createNewCategory) {
      formData.append('createNewCategory', 'true');
      formData.append('newCategoryName', this.newCategoryName);
      if (this.newCategoryDescription) {
        formData.append('newCategoryDescription', this.newCategoryDescription);
      }
    } else if (this.selectedCategoryId) {
      formData.append('selectedCategoryId', this.selectedCategoryId.toString());
    }

    const token = this.authService.getToken();
    console.log(token);
    if (!token) {
      this.message = 'No estás autenticado. Por favor, inicia sesión.';
      this.isSuccess = false;
      this.router.navigate(['/login']);
      return;
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}` // <<-- ¡Asegúrate de que esto sea CORRECTO!
    });

    this.http.post('http://localhost:9000/api/publicaciones/crear-con-imagen', formData, { headers: headers })
      .subscribe({
        next: (response) => {
          console.log('Publicación creada exitosamente', response);
          this.message = 'Publicación creada con éxito!';
          this.isSuccess = true;
          form.resetForm();
          this.publicacion = { titulo: '', contenido: '' };
          this.selectedImage = null;
          this.createNewCategory = false;
          this.selectedCategoryId = undefined;
          this.newCategoryName = '';
          this.newCategoryDescription = '';
          this.loadCategories();
          this.router.navigate(['/dashboard']);
        },
        error: (error) => {
          console.error('Error al crear la publicación:', error);
          this.message = 'Error al crear la publicación: ' + (error.error?.message || error.message || 'Error desconocido.');
          this.isSuccess = false;
        }
      });
  }
}