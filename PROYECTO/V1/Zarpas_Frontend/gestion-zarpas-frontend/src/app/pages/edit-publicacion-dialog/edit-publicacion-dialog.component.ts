// src/app/pages/edit-publicacion-dialog/edit-publicacion-dialog.component.ts
import { Component, OnInit, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // Necesario para ngModel
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon'; // Para mat-icon
import { MatDialogModule, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog'; // Para mat-dialog-content y mat-dialog-actions
import { MatSelectModule } from '@angular/material/select'; // Si usas mat-select para categorías
import { Publicacion} from '../../modelos/Publicacion'; // Importa Publicacion y Categoria
import { Categoria} from '../../modelos/Categoria';
import { PostService } from '../../services/post.service';
import { Observable } from 'rxjs'; // Importa Observable para tipar los next/error

@Component({
  selector: 'app-edit-publicacion-dialog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule, // Importa MatDialogModule para los componentes de diálogo
    MatSelectModule // Asegúrate de importar MatSelectModule si estás usando mat-select
  ],
  templateUrl: './edit-publicacion-dialog.component.html',
  styleUrl: './edit-publicacion-dialog.component.css'
})
export class EditPublicacionDialogComponent implements OnInit {
  publicacion: Publicacion;
  selectedFile: File | null = null;
  previewUrl: string | ArrayBuffer | null = null;
  categorias: Categoria[] = [];
  selectedCategory: Categoria | null = null; // Cambio a tipo Categoria

  constructor(
    public dialogRef: MatDialogRef<EditPublicacionDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { publicacion: Publicacion },
    private postService: PostService
  ) {
    // Asegúrate de que la publicación se clona para no modificar directamente el objeto original
    this.publicacion = { ...data.publicacion };
    // Carga la URL de la imagen existente si la hay
    if (this.publicacion.imagenUrl) {
      this.previewUrl = 'http://localhost:9000' + this.publicacion.imagenUrl;
    }
    // Asigna la categoría existente si la hay (asumiendo que 'categoria' existe en Publicacion)
    if (this.publicacion.categoria) {
      this.selectedCategory = this.publicacion.categoria;
    }
  }

  ngOnInit(): void {
    this.loadCategorias();
    // Preseleccionar la categoría si la publicación ya tiene una
    // Esta parte se movió al constructor para asegurar que selectedCategory se inicialice temprano
    // si publicacion.categoria ya tiene un valor.
    // Aunque si las categorías se cargan asincrónicamente, necesitarás un pequeño ajuste.
    // Lo más seguro es que se asigne en el subscribe de loadCategorias.
  }

  loadCategorias(): void {
    this.postService.getAllCategorias().subscribe({
      next: (data: Categoria[]) => { // Tipado explícito
        this.categorias = data;
        // Una vez que las categorías están cargadas, busca y preselecciona la categoría de la publicación
        if (this.publicacion.categoria && this.publicacion.categoria.idCategoria) {
            this.selectedCategory = this.categorias.find(
                cat => cat.idCategoria === this.publicacion.categoria?.idCategoria
            ) || null;
        }
      },
      error: (error: any) => { // Tipado explícito
        console.error('Error al cargar categorías:', error);
      }
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      const reader = new FileReader();
      reader.onload = () => {
        this.previewUrl = reader.result;
      };
      reader.readAsDataURL(this.selectedFile);
    } else {
      this.selectedFile = null;
      // Si no se selecciona un nuevo archivo, restablecer la vista previa a la imagen existente si la hay
      this.previewUrl = this.publicacion.imagenUrl ? 'http://localhost:9000' + this.publicacion.imagenUrl : null;
    }
  }

  onCategorySelected(event: any): void {
    const categoryId = event.value; // Obtén el ID de la categoría seleccionada
    this.selectedCategory = this.categorias.find(cat => cat.idCategoria === categoryId) || null;
  }

  onSave(): void {
    // Asigna la categoría seleccionada al objeto de publicación antes de guardar
    this.publicacion.categoria = this.selectedCategory || undefined; // Asegúrate de que Publicacion.categoria puede ser undefined o null

    // Lógica para guardar la publicación, ya sea actualizando o con nueva imagen
    if (this.selectedFile) {
      // Si se seleccionó un nuevo archivo, subirlo con la publicación
      const formData = new FormData();
      formData.append('titulo', this.publicacion.titulo);
      formData.append('contenido', this.publicacion.contenido);
      if (this.publicacion.idPublicacion) {
        formData.append('idPublicacion', this.publicacion.idPublicacion.toString());
      }
      if (this.publicacion.usuario && this.publicacion.usuario.idUsuario) {
        formData.append('idUsuario', this.publicacion.usuario.idUsuario.toString());
      }
      if (this.publicacion.categoria && this.publicacion.categoria.idCategoria) {
        formData.append('idCategoria', this.publicacion.categoria.idCategoria.toString());
      }
      formData.append('file', this.selectedFile);

      // Llama al método actualizado en PostService
      this.postService.actualizarPublicacionConImagen(this.publicacion.idPublicacion!, formData).subscribe({
        next: (response: Publicacion) => { // Tipado explícito
          this.dialogRef.close(response); // Cierra el diálogo con la publicación actualizada
        },
        error: (error: any) => { // Tipado explícito
          console.error('Error al actualizar publicación con imagen:', error);
          // Opcional: mostrar un mensaje de error al usuario
        }
      });

    } else {
      // Si no hay nuevo archivo, solo actualizar la publicación sin cambiar la imagen
      this.postService.actualizarPublicacion(this.publicacion).subscribe({
        next: (response: Publicacion) => { // Tipado explícito
          this.dialogRef.close(response); // Cierra el diálogo con la publicación actualizada
        },
        error: (error: any) => { // Tipado explícito
          console.error('Error al actualizar publicación:', error);
          // Opcional: mostrar un mensaje de error al usuario
        }
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close(); // Cierra el diálogo sin guardar
  }
}