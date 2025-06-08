// src/app/components/navbar/navbar.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core'; // <-- Importa OnDestroy
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { SearchService } from '../../services/search.service';
import { Subscription } from 'rxjs'; // <-- ¡Importa Subscription!

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    CommonModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatDividerModule,
    RouterLink,
    FormsModule
  ],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit, OnDestroy { // <-- Implementa OnDestroy
  isLoggedIn: boolean = false;
  username: string | null = null;
  searchTerm: string = '';
  private authSubscription: Subscription = new Subscription(); // Para gestionar las suscripciones

  constructor(
    private authService: AuthService,
    private router: Router,
    // private cdr: ChangeDetectorRef, // Ya no es necesario si usas Observables correctamente
    private searchService: SearchService
  ) { }

  ngOnInit(): void {
    // Suscribirse al estado de login
    this.authSubscription.add(
      this.authService.isLoggedIn$.subscribe(loggedIn => {
        this.isLoggedIn = loggedIn;
        console.log('Navbar: Estado de login actualizado a:', loggedIn);
      })
    );

    // Suscribirse al nombre de usuario
    this.authSubscription.add(
      this.authService.currentUser$.subscribe(user => {
        this.username = user ? user.username : null;
        console.log('Navbar: Nombre de usuario actualizado a:', this.username);
      })
    );

    // Suscribirse al término de búsqueda para mantenerlo actualizado
    this.authSubscription.add( // Añade esta suscripción al mismo grupo
      this.searchService.searchTerm$.subscribe(term => {
        this.searchTerm = term;
      })
    );
  }

  // Importante: Desuscribirse para evitar fugas de memoria
  ngOnDestroy(): void {
    this.authSubscription.unsubscribe();
  }

  logout(): void {
    this.authService.logout(); // Llama al logout del servicio
    this.router.navigate(['/login']); // Redirige al login después de desloguear
  }

  onSearch(): void {
    this.searchService.updateSearchTerm(this.searchTerm);
    this.router.navigate(['/dashboard']);
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.searchService.updateSearchTerm('');
  }
}