// src/app/pages/dashboard/dashboard.component.ts
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common'; // Para directivas como *ngFor, *ngIf y pipes
import { MatButtonModule } from '@angular/material/button'; // Si usas botones de Material
import { PostService } from '../../services/post.service'; // Importa el servicio de publicaciones
import { Publicacion } from '../../modelos/Publicacion'; // Importa el modelo de publicación
import { HttpClientModule, HttpResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { ReaccionPublicacionService } from '../../services/reaccion-publicacion.service'; // Importa el servicio de reacciones
import { TipoReaccion } from '../../modelos/TipoReaccion';
import { AuthService } from '../../services/auth.service'; // ¡IMPORTA TU AUTHSERVICE!
import { forkJoin, Observable, of, throwError } from 'rxjs'; // ¡AÑADE 'throwError' AQUÍ!
import { map, catchError } from 'rxjs/operators';
import { ComentarioService } from '../../services/comentario.service'; // ¡NUEVO!
import { ComentarioReaccionService } from '../../services/comentario-reaccion.service'; // ¡NUEVO!
import { Comentario, ComentarioRequest } from '../../modelos/Comentario'; // ¡NUEVO!
import { ComentarioReaccionRequest } from '../../modelos/ComentarioReaccion'; // ¡NUEVO!
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-dashboard',
  standalone: true, // Asegúrate de que sea standalone si lo creaste así
  imports: [
    CommonModule,
    MatButtonModule,
    HttpClientModule,
    RouterLink,
    FormsModule
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'] // <-- CORREGIDO: de 'styleUrl' a 'styleUrls'
})
export class DashboardComponent implements OnInit {

  posts: Publicacion[] = []; // Array donde se almacenarán las publicaciones
  TipoReaccion = TipoReaccion; // Para usar el enum TipoReaccion en el template
  reaccionesConteo: { [publicacionId: number]: { like: number; dislike: number } } = {};
  currentUserReaction: { [publicacionId: number]: TipoReaccion | null } = {};

    // --- ¡NUEVO! Para comentarios ---
    nuevoComentarioTexto: { [publicacionId: number]: string } = {}; // Para el texto del nuevo comentario
    // Almacena conteos y reacciones de usuario por comentario
    comentarioReaccionesConteo: { [comentarioId: number]: { like: number, dislike: number } } = {};
    currentUserComentarioReaction: { [comentarioId: number]: TipoReaccion | null } = {};
  


  // Inyecta PostService, ReaccionPublicacionService y AuthService en el constructor
  constructor(
    private postService: PostService,
    private reaccionPublicacionService: ReaccionPublicacionService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef, // ¡INYECTA ChangeDetectorRef aquí!
    private comentarioService: ComentarioService, // ¡NUEVO!
    private comentarioReaccionService: ComentarioReaccionService
  ) { }

  ngOnInit(): void {
    this.loadPosts();
  }

  loadPosts(): void {
    this.postService.getPublicaciones().subscribe({
      next: (data: Publicacion[]) => {
        this.posts = data.map(post => {
          // Lógica de conversión de fechas existente
          let fechaCreacionDate: Date | null = null;
          if (post.fechaCreacion) {
            const parsedDate = new Date(post.fechaCreacion);
            if (!isNaN(parsedDate.getTime())) {
              fechaCreacionDate = parsedDate;
            } else {
              console.warn(`FechaCreacion inválida para el post ID ${post.idPublicacion || 'desconocido'}: "${post.fechaCreacion}". Se establecerá como null.`);
            }
          }

          let fechaModificacionDate: Date | null = null;
          if (post.fechaModificacion) {
            const parsedDate = new Date(post.fechaModificacion);
            if (!isNaN(parsedDate.getTime())) {
              fechaModificacionDate = parsedDate;
            } else {
              console.warn(`FechaModificacion inválida para el post ID ${post.idPublicacion || 'desconocido'}: "${post.fechaModificacion}". Se establecerá como null.`);
            }
          }

          return {
            ...post,
            fechaCreacion: fechaCreacionDate,
            fechaModificacion: fechaModificacionDate,
            comentarios: []
          };
        });
        console.log('Publicaciones cargadas:', this.posts);
        this.loadReactionsForPosts(); // Cargar reacciones después de cargar las publicaciones
        this.loadCommentsForPosts(); 
      },
      error: (error: any) => {
        console.error('Error al cargar publicaciones:', error);
      }
    });
  }

  // Método para cargar los conteos de reacciones y la reacción del usuario para todas las publicaciones
  loadReactionsForPosts(): void {
    const currentUserId = this.authService.getUser()?.id;

    if (!this.posts || this.posts.length === 0) {
      this.reaccionesConteo = {};
      this.currentUserReaction = {};
      this.cdr.detectChanges();
      return;
    }

    const conteoObservables: Observable<{ publicacionId: number, conteo: { like: number, dislike: number } }>[] = [];
    const userReactionObservables: Observable<{ publicacionId: number, reaction: TipoReaccion | null }>[] = [];

    this.posts.forEach(post => {
      conteoObservables.push(
        this.reaccionPublicacionService.getConteoReacciones(post.idPublicacion!).pipe(
          map(conteo => ({ publicacionId: post.idPublicacion!, conteo }))
        )
      );

      if (currentUserId) {
        userReactionObservables.push(
          this.reaccionPublicacionService.getReaccionUsuario(currentUserId, post.idPublicacion!).pipe(
            map(reaction => ({ publicacionId: post.idPublicacion!, reaction })),
            catchError(error => {
              if (error.status === 404) {
                return of({ publicacionId: post.idPublicacion!, reaction: null });
              }
              console.error(`Error al cargar reacción del usuario para la publicación ${post.idPublicacion}:`, error);
              return throwError(() => new Error(`Error loading user reaction for post ${post.idPublicacion}`));
            })
          )
        );
      }
    });
  
    // Usar forkJoin para esperar a que todas las peticiones de conteo terminen
    forkJoin(conteoObservables).subscribe({
      next: (results) => {
        const newReaccionesConteo: { [publicacionId: number]: { like: number, dislike: number } } = {};
        results.forEach(res => {
          newReaccionesConteo[res.publicacionId] = res.conteo;
        });
        this.reaccionesConteo = newReaccionesConteo;
        console.log('Conteos de reacciones cargados y actualizados (nueva referencia):', this.reaccionesConteo);

        if (userReactionObservables.length > 0) {
          forkJoin(userReactionObservables).subscribe({
            next: (userResults) => {
              const newCurrentUserReaction: { [publicacionId: number]: TipoReaccion | null } = {};
              userResults.forEach(res => {
                newCurrentUserReaction[res.publicacionId] = res.reaction;
              });
              this.currentUserReaction = newCurrentUserReaction;
              console.log('Reacciones de usuario cargadas y actualizadas (nueva referencia):', this.currentUserReaction);
              this.cdr.detectChanges();
            },
            error: (error) => console.error('Error general al cargar reacciones de usuario:', error)
          });
        } else {
          this.currentUserReaction = {};
          console.log('No hay usuario logueado, currentUserReaction está vacío.');
          this.cdr.detectChanges();
        }
      },
      error: (error) => console.error('Error general al cargar conteos de reacciones:', error)
    });
  }

  onToggleReaccion(idPublicacion: number, tipoReaccion: TipoReaccion): void {
    const currentUser = this.authService.getUser();
    if (!currentUser || !currentUser.id) {
      console.error('Usuario no autenticado.');
      return;
    }

    const idUsuario = currentUser.id;

    this.reaccionPublicacionService.toggleReaccion(idUsuario, idPublicacion, tipoReaccion).subscribe({
      next: (response: HttpResponse<any>) => { // ¡CAMBIO AQUÍ! Espera HttpResponse
        console.log('Respuesta del backend (HTTP Response):', response);

        let reactionResult: TipoReaccion | null = null;
        if (response.status === 200 && response.body) {
          // Si el estado es 200 y hay un cuerpo, significa que se creó o actualizó una reacción.
          // El cuerpo contiene el objeto ReaccionPublicacion, del que extraemos tipoReaccion.
          reactionResult = response.body.tipoReaccion; // Asume que el cuerpo es { id, tipoReaccion, ... }
          console.log('Reacción creada/actualizada:', reactionResult);
        } else if (response.status === 204) {
          // Si el estado es 204 No Content, significa que la reacción fue eliminada.
          reactionResult = null;
          console.log('Reacción eliminada (No Content).');
        } else {
          // Manejar códigos de estado inesperados o respuestas 200 vacías (que ahora deberían ser 204)
          console.warn('Respuesta inesperada del backend, forzando recarga de reacciones:', response);
        }

        // Actualiza currentUserReaction localmente
        this.currentUserReaction = {
          ...this.currentUserReaction,
          [idPublicacion]: reactionResult
        };
        console.log(`Estado final de currentUserReaction para la publicación ${idPublicacion} es:`, this.currentUserReaction[idPublicacion]);

        // Finalmente, recargar todos los conteos y reacciones de usuario
        this.loadReactionsForPosts();
      },
      error: (error) => {
        console.error('Error al procesar reacción:', error);
        // En caso de error, también intenta refrescar el estado para asegurar consistencia
        this.loadReactionsForPosts();
      }
    });
  }


  // Métodos auxiliares para el template
  userHasReacted(publicacionId: number): boolean {
    return this.currentUserReaction[publicacionId] !== null && this.currentUserReaction[publicacionId] !== undefined;
  }

  hasLiked(publicacionId: number): boolean {
    return this.currentUserReaction[publicacionId] === TipoReaccion.like;
  }

  hasDisliked(publicacionId: number): boolean {
    return this.currentUserReaction[publicacionId] === TipoReaccion.dislike;
  }

  loadCommentsForPosts(): void {
    if (!this.posts || this.posts.length === 0) {
      this.cdr.detectChanges();
      return;
    }

    // Usamos `forEach` y `flatMap` para aplanar los observables de comentarios
    // y luego `forkJoin` para esperar a que todos se carguen.
    const commentObservables: Observable<Comentario[]>[] = this.posts.map(post => {
      if (post.idPublicacion) {
        return this.comentarioService.obtenerComentariosPorPublicacion(post.idPublicacion).pipe(
          map(comentarios => {
            // Asigna los comentarios cargados a la publicación correspondiente
            const postIndex = this.posts.findIndex(p => p.idPublicacion === post.idPublicacion);
            if (postIndex !== -1) {
              this.posts[postIndex].comentarios = comentarios;
              this.nuevoComentarioTexto[post.idPublicacion] = ''; // Inicializar campo de texto
            }
            return comentarios; // Devuelve los comentarios para el siguiente paso (cargar reacciones)
          }),
          catchError(error => {
            console.error(`Error al cargar comentarios para la publicación ${post.idPublicacion}:`, error);
            // Si hay un error, devuelve un observable de un array vacío para no detener forkJoin
            return of([]);
          })
        );
      }
      return of([]); // Si post.idPublicacion es undefined, devuelve un observable vacío
    });

    forkJoin(commentObservables).subscribe({
      next: (allCommentsArrays) => {
        console.log('Todos los comentarios cargados para las publicaciones.');
        // flatMap para obtener una lista única de todos los comentarios
        const allComments = allCommentsArrays.flatMap(arr => arr);
        this.loadReactionsForComments(allComments); // Cargar reacciones para TODOS los comentarios
      },
      error: (error) => {
        console.error('Error general al cargar comentarios para las publicaciones:', error);
      }
    });
  }

  loadReactionsForComments(comments: Comentario[]): void {
    const currentUserId = this.authService.getUser()?.id;

    if (!comments || comments.length === 0) {
      this.comentarioReaccionesConteo = {};
      this.currentUserComentarioReaction = {};
      this.cdr.detectChanges();
      return;
    }

    const conteoObservables: Observable<{ comentarioId: number, conteo: { like: number, dislike: number } }>[] = [];
    const userReactionObservables: Observable<{ comentarioId: number, reaction: TipoReaccion | null }>[] = [];

    comments.forEach(comment => {
      if (comment.idComentario) {
        conteoObservables.push(
          this.comentarioReaccionService.getConteoReacciones(comment.idComentario).pipe(
            map(conteo => ({ comentarioId: comment.idComentario!, conteo }))
          )
        );

        if (currentUserId) {
          userReactionObservables.push(
            this.comentarioReaccionService.getReaccionUsuario(currentUserId, comment.idComentario).pipe(
              map(reaction => ({ comentarioId: comment.idComentario!, reaction })),
              catchError(error => {
                if (error.status === 404) {
                  return of({ comentarioId: comment.idComentario!, reaction: null });
                }
                console.error(`Error al cargar reacción del usuario para el comentario ${comment.idComentario}:`, error);
                return throwError(() => new Error(`Error loading user reaction for comment ${comment.idComentario}`));
              })
            )
          );
        }
      }
    });

    forkJoin(conteoObservables).subscribe({
      next: (results) => {
        const newComentarioReaccionesConteo: { [comentarioId: number]: { like: number, dislike: number } } = {};
        results.forEach(res => {
          newComentarioReaccionesConteo[res.comentarioId] = res.conteo;
        });
        this.comentarioReaccionesConteo = newComentarioReaccionesConteo;
        console.log('Conteos de reacciones de comentarios cargados y actualizados:', this.comentarioReaccionesConteo);

        if (userReactionObservables.length > 0) {
          forkJoin(userReactionObservables).subscribe({
            next: (userResults) => {
              const newCurrentUserComentarioReaction: { [comentarioId: number]: TipoReaccion | null } = {};
              userResults.forEach(res => {
                newCurrentUserComentarioReaction[res.comentarioId] = res.reaction;
              });
              this.currentUserComentarioReaction = newCurrentUserComentarioReaction;
              console.log('Reacciones de usuario a comentarios cargadas y actualizadas:', this.currentUserComentarioReaction);
              this.cdr.detectChanges(); // Forzar detección de cambios después de cargar todo
            },
            error: (error) => console.error('Error general al cargar reacciones de usuario a comentarios:', error)
          });
        } else {
          this.currentUserComentarioReaction = {};
          console.log('No hay usuario logueado, currentUserComentarioReaction está vacío.');
          this.cdr.detectChanges();
        }
      },
      error: (error) => console.error('Error general al cargar conteos de reacciones de comentarios:', error)
    });
  }

  onCrearComentario(idPublicacion: number): void {
    const currentUser = this.authService.getUser();
    if (!currentUser || !currentUser.id) {
      console.error('Usuario no autenticado.');
      return;
    }

    const textoComentario = this.nuevoComentarioTexto[idPublicacion];
    if (!textoComentario || textoComentario.trim() === '') {
      console.warn('El comentario no puede estar vacío.');
      return;
    }

    const request: ComentarioRequest = {
      idUsuario: currentUser.id,
      idPublicacion: idPublicacion,
      texto: textoComentario.trim()
    };

    this.comentarioService.crearComentario(request).subscribe({
      next: (comentarioCreado: Comentario) => {
        console.log('Comentario creado:', comentarioCreado);
        // Encuentra la publicación y añade el comentario
        const postIndex = this.posts.findIndex(p => p.idPublicacion === idPublicacion);
        if (postIndex !== -1 && this.posts[postIndex].comentarios) {
          // Asigna el usuario al comentario para que el HTML pueda mostrar el username
          comentarioCreado.usuario = currentUser;
          // Inicializa las reacciones del nuevo comentario
          comentarioCreado.reaccionesConteo = { like: 0, dislike: 0 };
          comentarioCreado.currentUserReaction = null;

          this.posts[postIndex].comentarios!.push(comentarioCreado);
          this.nuevoComentarioTexto[idPublicacion] = ''; // Limpiar el campo de texto
          this.cdr.detectChanges(); // Forzar actualización de la vista
          this.loadReactionsForComments([comentarioCreado]); // Cargar reacciones para el nuevo comentario
        }
      },
      error: (error) => {
        console.error('Error al crear comentario:', error);
      }
    });
  }

  onToggleComentarioReaccion(idComentario: number, tipoReaccion: TipoReaccion): void {
    const currentUser = this.authService.getUser();
    if (!currentUser || !currentUser.id) {
      console.error('Usuario no autenticado.');
      return;
    }

    const request: ComentarioReaccionRequest = {
      idUsuario: currentUser.id,
      idComentario: idComentario,
      tipoReaccion: tipoReaccion
    };

    this.comentarioReaccionService.toggleReaccion(request).subscribe({
      next: (response: HttpResponse<any>) => {
        console.log('Respuesta del backend (HTTP Response para comentario):', response);

        let reactionResult: TipoReaccion | null = null;
        if (response.status === 200 && response.body) {
          reactionResult = response.body.tipoReaccion;
          console.log('Reacción de comentario creada/actualizada:', reactionResult);
        } else if (response.status === 204) {
          reactionResult = null;
          console.log('Reacción de comentario eliminada (No Content).');
        } else {
          console.warn('Respuesta inesperada del backend para reacción de comentario, forzando recarga:', response);
        }

        // Actualiza currentUserComentarioReaction localmente
        this.currentUserComentarioReaction = {
          ...this.currentUserComentarioReaction,
          [idComentario]: reactionResult
        };
        console.log(`Estado final de currentUserComentarioReaction para el comentario ${idComentario} es:`, this.currentUserComentarioReaction[idComentario]);

        // Encuentra el comentario afectado para forzar una carga de reacciones solo para él
        let affectedComment: Comentario | undefined;
        for (const post of this.posts) {
            affectedComment = post.comentarios?.find(c => c.idComentario === idComentario);
            if (affectedComment) break;
        }

        if (affectedComment) {
          this.loadReactionsForComments([affectedComment]); // Recargar solo las reacciones de este comentario
        } else {
          // Si no se encuentra el comentario (raro, pero como fallback) recarga todo
          this.loadCommentsForPosts(); // Esto recargará todos los comentarios y sus reacciones
        }
      },
      error: (error) => {
        console.error('Error al procesar reacción de comentario:', error);
        // En caso de error, recarga las reacciones para ese comentario
        let affectedComment: Comentario | undefined;
        for (const post of this.posts) {
            affectedComment = post.comentarios?.find(c => c.idComentario === idComentario);
            if (affectedComment) break;
        }
        if (affectedComment) {
          this.loadReactionsForComments([affectedComment]);
        } else {
          this.loadCommentsForPosts();
        }
      }
    });
  }

  hasComentarioLiked(idComentario: number): boolean {
    return this.currentUserComentarioReaction[idComentario] === TipoReaccion.like;
  }

  hasComentarioDisliked(idComentario: number): boolean {
    return this.currentUserComentarioReaction[idComentario] === TipoReaccion.dislike;
  }

  // Helper para obtener el usuario actual más fácilmente en el HTML (opcional)
  getCurrentUser(): any {
    return this.authService.getUser();
  }
}
