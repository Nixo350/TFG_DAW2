import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; // Para directivas como *ngIf
import { FormsModule } from '@angular/forms'; // Necesario para [(ngModel)]
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card'; // Para el diseño de la tarjeta
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar'; // Para notificaciones
import { AuthService } from '../../services/auth.service'; // Asegúrate de que esta ruta sea correcta
import { Router } from '@angular/router'; // Para la navegación programática

@Component({
  selector: 'app-login',
  standalone: true, // Si tu proyecto es standalone
  imports: [
    CommonModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatSnackBarModule // Asegúrate de que este módulo esté aquí
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css' // Usar styleUrl si es un archivo CSS
})
export class LoginComponent implements OnInit {

  // Objeto para almacenar las credenciales del usuario
  user = {
    username: '',
    password: '' // Importante: Coincide con 'password' en LoginRequest de Spring Boot
  };

  isLoggedIn = false;
  isLoginFailed = false;
  errorMessage = '';
  roles: string[] = [];

  constructor(
    private authService: AuthService,
    private snack: MatSnackBar, // Inyecta MatSnackBar
    private router: Router // Inyecta Router
  ) { }

  ngOnInit(): void {
    // Opcional: Si quieres verificar si el usuario ya está logueado al cargar el componente.
    // Esto depende de cómo implementes el AuthService y si el token se valida.
    // if (this.authService.isLoggedIn()) {
    //   this.isLoggedIn = true;
    //   this.roles = this.authService.getUserRoles();
    //   // Podrías redirigir automáticamente si ya está logueado
    //   // this.router.navigate(['/dashboard']);
    // }
  }

  formSubmit() {
    console.log('Intentando iniciar sesión...');
    console.log('Credenciales enviadas:', this.user);

    // 1. Validaciones básicas del formulario en el cliente
    if (this.user.username.trim() === '' || this.user.username === null) {
      this.snack.open('El nombre de usuario es requerido !!', 'Aceptar', {
        duration: 3000, // Muestra el mensaje por 3 segundos
        verticalPosition: 'top',
        horizontalPosition: 'right'
      });
      return; // Detiene la ejecución si falla la validación
    }

    if (this.user.password.trim() === '' || this.user.password === null) {
      this.snack.open('La contraseña es requerida !!', 'Aceptar', {
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'right'
      });
      return;
    }

    // 2. Llamar al servicio de autenticación para enviar las credenciales al backend
    this.authService.login(this.user.username, this.user.password).subscribe({
      next: (data: any) => {
        // Se ejecuta si la petición al backend es exitosa (código 2xx)
        console.log('Respuesta exitosa del backend (login):', data);
        this.snack.open('¡Inicio de sesión exitoso!', 'Cerrar', {
          duration: 3000,
          verticalPosition: 'top',
          horizontalPosition: 'right'
        });

        // Actualiza el estado del componente
        this.isLoginFailed = false;
        this.isLoggedIn = true;
        this.roles = data.roles; // Asume que la respuesta del backend incluye los roles

        // 3. Redirige al usuario a una página principal o dashboard
        this.router.navigate(['/dashboard']); // ¡IMPORTANTE! Ajusta esta ruta a tu dashboard
      },
      error: (e: any) => {
        // Se ejecuta si la petición al backend devuelve un error (código 4xx, 5xx)
        console.error('Error durante el inicio de sesión:', e);
        this.isLoginFailed = true; // Marca el login como fallido
        // Intenta extraer un mensaje de error más específico del backend
        this.errorMessage = e.error?.message || 'Credenciales inválidas. Por favor, inténtalo de nuevo.';

        this.snack.open('Error al iniciar sesión: ' + this.errorMessage, 'Cerrar', {
          duration: 5000,
          verticalPosition: 'top',
          horizontalPosition: 'right'
        });
      }
    });
  }
}