import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service'; 
import { AuthService } from '../../services/auth.service'; 
import { PostService } from '../../services/post.service'; 
import { HttpErrorResponse } from '@angular/common/http';
import { Router,RouterModule  } from '@angular/router'; 
import { Subject, takeUntil } from 'rxjs'; 
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ReactiveFormsModule } from '@angular/forms'; 
import { HttpClientModule } from '@angular/common/http';

import { SlicePipe } from '@angular/common';


@Component({
  selector: 'app-profile',
  standalone: true, 
  imports: [
    CommonModule,
    HttpClientModule,
    HttpClientModule,
    RouterModule, 
    MatIconModule,      
    MatButtonModule,    
    MatDividerModule, 
    MatFormFieldModule, 
    SlicePipe,
    MatProgressSpinnerModule,
    ReactiveFormsModule,
     MatInputModule  ,
     MatCardModule,  
  ],
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']
})
export class ProfileComponent implements OnInit, OnDestroy {
  currentUser: any;
  currentProfileImageUrl: string | null = null;
  myPublications: any[] = [];
  myPublicationsCount = 0;
  savedPublicationsCount = 0;

  errorMessage = '';
  successMessage = '';

  private destroy$ = new Subject<void>();

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private publicacionService: PostService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.authService.currentUser$.pipe(takeUntil(this.destroy$)).subscribe(user => {
      this.currentUser = user;
      if (this.currentUser && this.currentUser.id) {
        console.log('currentUser.fotoPerfil:', this.currentUser.fotoPerfil);

      
        this.loadUserProfile();
        this.loadUserPublications();
        // this.loadSavedPublications();
      } else {
        this.errorMessage = 'No hay usuario logueado o información de ID disponible.';
      }
    });

  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadUserProfile(): void {
    this.userService.getUserById(this.currentUser.id).subscribe(
      data => {
        this.currentUser = { ...this.currentUser, ...data };
      },
      (err: HttpErrorResponse) => {
        this.errorMessage = err.error?.message || 'Error al cargar los datos del perfil.';
        console.error('Error al cargar datos del usuario:', err);
      }
    );
  }

  loadUserPublications(): void {
    if (this.currentUser && this.currentUser.id) {
      this.publicacionService.getPublicacionesByUserId(this.currentUser.id).pipe(takeUntil(this.destroy$)).subscribe(
        data => {
          this.myPublications = data;
          this.myPublicationsCount = data.length;
        },
        (err: HttpErrorResponse) => {
          console.error('Error al cargar publicaciones:', err);
        }
      );
    }
  }

  onDeletePublication(publicationId: number): void {
    if (confirm('¿Estás seguro de que quieres eliminar esta publicación?')) {
      this.publicacionService.deletePublicacion(publicationId).pipe(takeUntil(this.destroy$)).subscribe(
        () => {
          this.successMessage = 'Publicación eliminada con éxito.';
          this.loadUserPublications(); // Recargar publicaciones
        },
        (err: HttpErrorResponse) => {
          this.errorMessage = err.error?.message || 'Error al eliminar la publicación.';
          console.error('Error al eliminar publicación:', err);
        }
      );
    }
  }

  onDeleteAccount(): void {
    if (confirm('¡ADVERTENCIA! Estás a punto de eliminar tu cuenta permanentemente. ¿Estás absolutamente seguro?')) {
      if (this.currentUser && this.currentUser.id) {
        this.userService.deleteUser(this.currentUser.id).pipe(takeUntil(this.destroy$)).subscribe(
          () => {
            this.successMessage = 'Cuenta eliminada con éxito. Redirigiendo a la página de inicio.';
            this.authService.logout(true);
            this.router.navigate(['/dashboard']);
          },
          (err: HttpErrorResponse) => {
            this.errorMessage = err.error?.message || 'Error al eliminar la cuenta.';
            console.error('Error al eliminar la cuenta:', err);
          }
        );
      }
    }
  }
}