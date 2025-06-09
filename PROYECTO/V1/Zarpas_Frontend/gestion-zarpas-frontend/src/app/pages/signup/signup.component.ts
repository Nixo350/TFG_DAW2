import { Component, OnInit } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common'; 
import { Router } from '@angular/router';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css'],
  standalone: true,
  imports: [
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    FormsModule,
    MatSnackBarModule,
    CommonModule 
  ]
})
export class SignupComponent implements OnInit {

  public user = {
    username: '',
    contrasena: '',
    nombre: '',
    email: ''
  };

  constructor(private userService: AuthService, private snack: MatSnackBar,private router: Router) {}

  ngOnInit(): void {}

  formSubmit() {
    console.log("Datos del formulario a enviar:", this.user);

    if (this.user.username.trim() === '' || this.user.username === null) {
      this.snack.open('El nombre de usuario es requerido !!', 'Aceptar', { duration: 3000 });
      return;
    }
    if (this.user.contrasena.trim() === '' || this.user.contrasena === null) {
      this.snack.open('La contraseña es requerida !!', 'Aceptar', { duration: 3000 });
      return;
    }
    if (this.user.contrasena.length < 6 || this.user.contrasena.length > 40) {
        this.snack.open('La contraseña debe tener entre 6 y 40 caracteres !!', 'Aceptar', { duration: 3000 });
        return;
    }
    if (this.user.email.trim() === '' || this.user.email === null) {
      this.snack.open('El email es requerido !!', 'Aceptar', { duration: 3000 });
      return;
    }

    this.userService.registerUser(this.user).subscribe(
      (data: any) => {
        console.log("Respuesta del backend:", data);
        this.snack.open('¡Usuario registrado con éxito!', 'Cerrar', { duration: 3000 });

        this.user = { username: '', contrasena: '', nombre: '', email: '' };
        this.router.navigate(['/dashboard']);
      },
      (error) => {
        console.error("Error al registrar usuario:", error);
        let errorMessage = 'Error al registrar el usuario. Inténtalo de nuevo.';
        if (error.status === 409) {
          errorMessage = 'El nombre de usuario o el email ya existen.';
        } else if (error.error && typeof error.error === 'string') {

          errorMessage = error.error;
        } else if (error.error && error.error.message) {
  
          errorMessage = error.error.message;
        }
        this.snack.open(errorMessage, 'Cerrar', { duration: 3000 });
      }
    );
  }
}