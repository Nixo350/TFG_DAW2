import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; // Necesario para *ngIf
import { RouterModule, Router } from '@angular/router'; // Para routerLink y la navegación programática
import { MatButtonModule } from '@angular/material/button'; // Para botones de Material Design
import { MatToolbarModule } from '@angular/material/toolbar'; // Para la barra de herramientas de Material Design
import { MatIconModule } from '@angular/material/icon'; // Para iconos de Material Design

import { AuthService } from '../../services/auth.service'; // Asegúrate de que la ruta sea correcta

@Component({
  selector: 'app-navbar',
  standalone: true, // Componente standalone
  imports: [
    CommonModule,
    RouterModule, // Importa RouterModule para routerLink
    MatButtonModule,
    MatToolbarModule,
    MatIconModule
  ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit {

  isLoggedIn: boolean = false;
  username: string | null = null;
  roles: string[] = [];

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    // Inicializa el estado de login al cargar el componente
    this.checkLoginStatus();

    // Puedes añadir aquí una suscripción a eventos de login/logout si tu AuthService los emite,
    // o simplemente llamar a checkLoginStatus en los puntos donde el estado pueda cambiar (ej. después de login/logout).
    // Para esta configuración simple, lo revisaremos en el logout.
  }

  // Método para actualizar el estado de login y la información del usuario
  checkLoginStatus(): void {
    this.isLoggedIn = this.authService.isLoggedIn();
    if (this.isLoggedIn) {
      const user = this.authService.getUser();
      this.username = user?.username || null;
      this.roles = user?.roles || [];
    } else {
      this.username = null;
      this.roles = [];
    }
  }

  // Método para cerrar sesión
  logout(): void {
    this.authService.logout();
    this.checkLoginStatus(); // Actualiza el estado después del logout
    this.router.navigate(['/login']); // Redirige al usuario a la página de login
  }

  // Opcional: Para mostrar/ocultar elementos basados en roles
  hasRole(roleName: string): boolean {
    return this.roles.includes(roleName);
  }
}