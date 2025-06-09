import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms'; 
import { MatFormFieldModule } from '@angular/material/form-field'; 
import { MatInputModule } from '@angular/material/input';      
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon'; 
import { RouterModule, Router } from '@angular/router'; 

import { UserService } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';
import { HttpErrorResponse } from '@angular/common/http';
import { Subject, takeUntil } from 'rxjs';

import { MatDividerModule } from '@angular/material/divider';

@Component({
  selector: 'app-edit-profile',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule, 
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDividerModule,
    MatIconModule,
    RouterModule 
  ],
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.css']
})
export class EditProfileComponent implements OnInit, OnDestroy {
  profileForm!: FormGroup;
  passwordForm!: FormGroup;
  currentUser: any;
  currentProfileImageUrl: string | null = null; // Para mostrar la imagen actual
  selectedFile: File | null = null; // Para almacenar el archivo seleccionado
  selectedFileName: string | null = null; // Para mostrar el nombre del archivo
  fileUploadError: string | null = null; // Para errores de subida de archivo

  errorMessage = '';
  successMessage = '';

  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.pipe(takeUntil(this.destroy$)).subscribe(user => {
      this.currentUser = user;
      if (this.currentUser && this.currentUser.id) {
        this.loadUserProfile();
        this.currentProfileImageUrl = this.currentUser.fotoPerfil || null; // Carga la imagen actual
      } else {
        this.errorMessage = 'No hay usuario logueado o información de ID disponible.';
        // Opcional: redirigir a login si no hay usuario
        this.router.navigate(['/login']);
      }
    });

    this.profileForm = this.fb.group({
      username: ['', Validators.required],
      email: [{ value: '', disabled: true }] // Email puede ser no editable aquí
    });

    this.passwordForm = this.fb.group({
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    }, { validators: this.passwordMatchValidator });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadUserProfile(): void {
    this.userService.getUserById(this.currentUser.id).pipe(takeUntil(this.destroy$)).subscribe(
      data => {
        this.currentUser = { ...this.currentUser, ...data };
        this.profileForm.patchValue({
          username: this.currentUser.username,
          email: this.currentUser.email
        });
        this.currentProfileImageUrl = this.currentUser.fotoPerfil || null; // Actualiza la URL de la imagen de perfil
      },
      (err: HttpErrorResponse) => {
        this.errorMessage = err.error?.message || 'Error al cargar los datos del perfil.';
        console.error('Error al cargar datos del usuario:', err);
      }
    );
  }

  // --- Manejo de la subida de archivo de imagen ---
  onFileSelected(event: Event): void {
    const element = event.currentTarget as HTMLInputElement;
    let fileList: FileList | null = element.files;
    this.fileUploadError = null; // Limpiar errores previos

    if (fileList && fileList.length > 0) {
      this.selectedFile = fileList[0];
      this.selectedFileName = this.selectedFile.name;

      // Opcional: Mostrar una previsualización de la nueva imagen seleccionada
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.currentProfileImageUrl = e.target.result; // Actualiza la previsualización
      };
      reader.readAsDataURL(this.selectedFile);
    } else {
      this.selectedFile = null;
      this.selectedFileName = null;
      this.currentProfileImageUrl = this.currentUser.fotoPerfil || null; // Restaura la imagen si no se seleccionó nada
    }
  }

  // --- Actualizar Perfil (ahora puede incluir la subida de imagen) ---
  onUpdateProfile(): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.fileUploadError = null;

    if (this.profileForm.invalid && !this.selectedFile) {
      // Si el formulario no es válido Y no se ha seleccionado un archivo
      this.errorMessage = 'Por favor, rellena los campos correctamente o selecciona una nueva imagen.';
      this.profileForm.markAllAsTouched();
      return;
    }

    if (this.currentUser && this.currentUser.id) {
      const updatedUsername = this.profileForm.get('username')?.value;
      const dataToUpdate = { username: updatedUsername };

      if (this.selectedFile) {
        // Si hay un archivo seleccionado, usa el nuevo método para subirlo
        this.userService.updateUserProfileWithImage(this.currentUser.id, updatedUsername, this.selectedFile)
          .pipe(takeUntil(this.destroy$))
          .subscribe(
            response => {
              this.successMessage = 'Perfil actualizado con éxito.';
              this.authService.updateCurrentUser(response); // Actualiza el usuario en AuthService
              this.router.navigate(['/profile']); // Redirige de vuelta al perfil
            },
            (err: HttpErrorResponse) => {
              this.errorMessage = err.error?.message || 'Error al actualizar el perfil y la imagen.';
              this.fileUploadError = 'Error al subir la imagen: ' + (err.error?.message || 'Fallo desconocido.');
              console.error('Error actualizando perfil con imagen:', err);
            }
          );
      } else {
        // Si no hay archivo seleccionado, solo actualiza el nombre de usuario
        this.userService.updateUserProfile(this.currentUser.id, dataToUpdate)
          .pipe(takeUntil(this.destroy$))
          .subscribe(
            response => {
              this.successMessage = 'Perfil actualizado con éxito.';
              this.authService.updateCurrentUser(response); // Actualiza el usuario en AuthService
              this.router.navigate(['/profile']); // Redirige de vuelta al perfil
            },
            (err: HttpErrorResponse) => {
              this.errorMessage = err.error?.message || 'Error al actualizar el perfil.';
              console.error('Error actualizando perfil:', err);
            }
          );
      }
    }
  }


  // --- Cambiar Contraseña ---
  onChangePassword(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.passwordForm.invalid) {
      this.errorMessage = 'Por favor, asegúrate de que las contraseñas coincidan y cumplan los requisitos.';
      this.passwordForm.markAllAsTouched();
      return;
    }

    if (this.currentUser && this.currentUser.id) {
      const newPassword = this.passwordForm.get('newPassword')?.value;
      this.userService.changeUserContrasena(this.currentUser.id, newPassword)
        .pipe(takeUntil(this.destroy$))
        .subscribe(
          () => {
            this.successMessage = 'Contraseña cambiada con éxito.';
            this.passwordForm.reset(); // Limpiar el formulario
          },
          (err: HttpErrorResponse) => {
            this.errorMessage = err.error?.message || 'Error al cambiar la contraseña.';
            console.error('Error cambiando contraseña:', err);
          }
        );
    }
  }

  // --- Validador de Contraseñas ---
  passwordMatchValidator(form: FormGroup) {
    const newPassword = form.get('newPassword')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;

    return newPassword === confirmPassword ? null : { mismatch: true };
  }
}