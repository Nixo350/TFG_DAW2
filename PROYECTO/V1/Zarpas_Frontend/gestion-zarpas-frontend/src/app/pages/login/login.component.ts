// src/app/pages/login/login.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatSnackBarModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {

  user = {
    username: '',
    password: ''
  };

  isLoggedIn = false; 
  isLoginFailed = false;
  errorMessage = '';
  roles: string[] = [];

  constructor(
    private authService: AuthService,
    private snack: MatSnackBar,
    private router: Router
  ) { }

  ngOnInit(): void {

    if (this.authService.isLoggedIn()) {
      this.isLoggedIn = true;
      const user = this.authService.getUserFromLocalStorage(); 
      if (user && user.roles) {
        this.roles = user.roles;
      }
      this.router.navigate(['/dashboard']); 
    }
  }

  goToSignup(): void {
    this.router.navigate(['/signup']);
  }

  formSubmit() {
    console.log('Intentando iniciar sesión...');
    console.log('Credenciales enviadas:', this.user);

    if (this.user.username.trim() === '' || this.user.username === null) {
      this.snack.open('El nombre de usuario es requerido !!', 'Aceptar', {
        duration: 3000, verticalPosition: 'top', horizontalPosition: 'right'
      });
      return;
    }

    if (this.user.password.trim() === '' || this.user.password === null) {
      this.snack.open('La contraseña es requerida !!', 'Aceptar', {
        duration: 3000, verticalPosition: 'top', horizontalPosition: 'right'
      });
      return;
    }

    this.authService.login(this.user.username, this.user.password).subscribe({
      next: (data: any) => {
        console.log('Respuesta exitosa del backend (login):', data);
        this.snack.open('¡Inicio de sesión exitoso!', 'Cerrar', {
          duration: 3000, verticalPosition: 'top', horizontalPosition: 'right'
        });

       
        this.isLoginFailed = false;
        this.isLoggedIn = true;
        this.roles = data.roles;

        this.router.navigate(['/dashboard']); 
      },
      error: (e: any) => {
        console.error('Error durante el inicio de sesión:', e);
        this.isLoginFailed = true;
        this.errorMessage = e.error?.message || 'Credenciales inválidas. Por favor, inténtalo de nuevo.';

        this.snack.open('Error al iniciar sesión: ' + this.errorMessage, 'Cerrar', {
          duration: 5000, verticalPosition: 'top', horizontalPosition: 'right'
        });
      }
    });
  }
}