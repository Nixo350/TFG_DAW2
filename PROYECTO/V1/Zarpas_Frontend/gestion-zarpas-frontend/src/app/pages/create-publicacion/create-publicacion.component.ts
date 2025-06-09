import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgForm, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

interface Publicacion {
  titulo: string;
  contenido: string;
}

interface Categoria {
  idCategoria: number;
  nombre: string;
  descripcion?: string; // Opcional
}

@Component({
  selector: 'app-create-publicacion',
  templateUrl: './create-publicacion.component.html',
  styleUrls: ['./create-publicacion.component.css'],
  standalone: true,
  imports: [
    FormsModule,
    CommonModule
  ]
})
export class CreatePublicacionComponent implements OnInit {
  publicacion: Publicacion = { titulo: '', contenido: '' };

  selectedImage: File | null = null;

  createNewCategory: boolean = false;
  selectedCategoryId: number | undefined = undefined;
  newCategoryName: string = '';
  newCategoryDescription: string = '';

  categories: Categoria[] = [];

  isSuccess: boolean | null = null;
  message: string = '';

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadCategories();
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

    if ((!this.selectedCategoryId && !this.createNewCategory) || (this.createNewCategory && (!this.newCategoryName || this.newCategoryName.trim() === ''))) {
      this.message = 'Por favor, selecciona o introduce un nombre para la categoría.';
      this.isSuccess = false;
      return;
    }

    if (form.invalid) {
      this.message = 'Por favor, completa todos los campos requeridos.';
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

    let categoriaNameToSend: string | undefined;

    if (this.createNewCategory) {
      categoriaNameToSend = this.newCategoryName;
    } else if (this.selectedCategoryId) {
      const selectedCategory = this.categories.find(cat => cat.idCategoria === this.selectedCategoryId);
      if (selectedCategory) {
        categoriaNameToSend = selectedCategory.nombre;
      } else {
        this.message = 'Categoría seleccionada no encontrada. Intenta recargar la página.';
        this.isSuccess = false;
        return;
      }
    }

    if (categoriaNameToSend && categoriaNameToSend.trim() !== '') {
      formData.append('categoriaNombre', categoriaNameToSend);
    } else {
      this.message = 'El nombre de la categoría es obligatorio.';
      this.isSuccess = false;
      return;
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
      'Authorization': `Bearer ${token}`
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