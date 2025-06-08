// src/app/pages/dashboard/dashboard.component.ts
import { Component, OnInit, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { PostService } from '../../services/post.service';
import { Publicacion } from '../../modelos/Publicacion';
import { HttpClientModule, HttpResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { ReaccionPublicacionService } from '../../services/reaccion-publicacion.service';
import { TipoReaccion } from '../../modelos/TipoReaccion';
import { AuthService } from '../../services/auth.service';
import { forkJoin, Observable, of, throwError, Subscription } from 'rxjs';
import { map, catchError, switchMap } from 'rxjs/operators';
import { ComentarioService } from '../../services/comentario.service';
import { ComentarioReaccionService } from '../../services/comentario-reaccion.service';
import { Comentario, ComentarioRequest } from '../../modelos/Comentario';
import { ComentarioReaccionRequest } from '../../modelos/ComentarioReaccion';
import { FormsModule } from '@angular/forms';
import { SearchService } from '../../services/search.service';
import { Categoria } from '../../modelos/Categoria';



@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    HttpClientModule,
    RouterLink,
    FormsModule,
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent implements OnInit, OnDestroy {
  public TipoReaccion = TipoReaccion;
  posts: Publicacion[] = [];
  publicacionReacciones: { [key: number]: { like: number; dislike: number } } = {};
  comentarioReacciones: { [key: number]: { like: number; dislike: number } } = {};
  showComments: { [key: number]: boolean } = {};
  categories: Categoria[] = [];
  currentUserPublicacionReaction: { [key: number]: TipoReaccion | null } = {};
  currentUserComentarioReaction: { [key: number]: TipoReaccion | null } = {};

  public searchTerm: string = '';
  private searchSubscription: Subscription | null = null; // <-- CORRECCIÓN: Inicializado a null

  public nuevoComentarioTexto: { [key: number]: string } = {};

  selectedCategoryName: string | null = null;
  selectedCategoryId: number | null = null;


  constructor(
    private postService: PostService,
    private reaccionPublicacionService: ReaccionPublicacionService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private comentarioService: ComentarioService,
    private comentarioReaccionService: ComentarioReaccionService,
    private searchService: SearchService
  ) {}

  ngOnInit(): void {
    this.loadPosts();
    this.loadAllCategorias();

    this.searchSubscription = this.searchService.searchTerm$.subscribe(
      (term) => {
        this.searchTerm = term;
        this.performSearch();
      }
    );

  }
  loadAllCategorias(): void {
    this.postService.getAllCategorias().subscribe({
      next: (data) => {
        this.categories = data;
        console.log('Categorías cargadas:', this.categories);
      },
      error: (error) => {
        console.error('Error al cargar las categorías:', error);
      },
    });
  }
  filterByCategory(): void {
    if (this.selectedCategoryId) {
      // Necesitarías un endpoint en el backend que filtre por ID de categoría
      // Por ahora, tu backend filtra por nombre, así que tendrías que encontrar el nombre
      const selectedCategory = this.categories.find(cat => cat.idCategoria === this.selectedCategoryId);
      if (selectedCategory) {
        this.postService.getPublicacionesByCategoria(selectedCategory.nombre).subscribe({
          next: (data) => {
            this.posts = data;
            this.cdr.detectChanges();
          },
          error: (error) => {
            console.error('Error al filtrar publicaciones por categoría:', error);
          },
        });
      }
    } else {
      this.loadPosts(); // Carga todas las publicaciones si no hay categoría seleccionada
    }
  }
  

  ngOnDestroy(): void {
    if (this.searchSubscription) {
      this.searchSubscription.unsubscribe();
    }
  }

  loadCommentsForPosts(posts: Publicacion[]): void {
    posts.forEach(post => {
      this.comentarioService.obtenerComentariosPorPublicacion(post.idPublicacion);
    });
  }
  loadPosts(): void {
    this.postService.getPublicaciones().subscribe({
      next: (data) => {
        this.posts = data;
        this.posts.forEach(post => {
          this.showComments[post.idPublicacion] = false; // Se establece a false explícitamente
          this.nuevoComentarioTexto[post.idPublicacion] = '';
      });
        this.loadReactionsForPosts();
        this.loadCommentsForPosts(this.posts);
      },
      error: (error) => {
        console.error('Error al cargar publicaciones:', error);
      },
    });
  }
  

  toggleComments(publicacionId: number): void {
    this.showComments[publicacionId] = !this.showComments[publicacionId]; // Invierte el valor
    if (this.showComments[publicacionId] && publicacionId) {
        this.comentarioService.obtenerComentariosPorPublicacion(publicacionId);
    }
}

  performSearch(): void {
    if (this.searchTerm && this.searchTerm.trim() !== '') {
      this.postService.searchPublicaciones(this.searchTerm).subscribe({
        next: (data) => {
          this.posts = data;
          this.loadReactionsForPosts();
        },
        error: (error) => {
          console.error('Error al buscar publicaciones:', error);
        },
      });
    } else {
      this.loadPosts();
    }
  }



  clearCategoryFilter(): void {
    this.selectedCategoryName = null;
    this.loadPosts();
  }

  loadReactionsForPosts(): void {
    const postReactionObservables: Observable<any>[] = [];
    const currentUser = this.authService.getUser();
    const currentUserId = currentUser ? currentUser.id : null;

    this.posts.forEach((post) => {
      // 1. Obtener conteo de likes/dislikes para la publicación
      if (post.idPublicacion === undefined || post.idPublicacion === null) {
        console.warn('Publicación sin idPublicacion definida, saltando carga de reacciones:', post);
        return; // Salta esta publicación si su ID no está definido
      }

      postReactionObservables.push(
        this.reaccionPublicacionService
          .getConteoReacciones(post.idPublicacion)
          .pipe(
            map((conteo) => {
              this.publicacionReacciones[post.idPublicacion!] = {
                like: conteo.like,
                dislike: conteo.dislike,
              };
              return conteo;
            }),
            catchError((err) => {
              console.error(
                `Error al cargar conteo de reacciones para la publicación ${post.idPublicacion}:`,
                err
              );
              this.publicacionReacciones[post.idPublicacion!] = {
                like: 0,
                dislike: 0,
              };
              return of(null);
            })
          )
      );

      // 2. Obtener la reacción del usuario actual para la publicación
      if (currentUserId) {
        postReactionObservables.push(
          this.reaccionPublicacionService
            .getReaccionUsuario(currentUserId, post.idPublicacion)
            .pipe(
              map((reaccion) => {
                this.currentUserPublicacionReaction[post.idPublicacion!] =
                  reaccion;
                return reaccion;
              }),
              catchError((err) => {
                if (err.status !== 404) {
                  console.error(
                    `Error al cargar reacción de usuario para publicación ${post.idPublicacion}:`,
                    err
                  );
                }
                this.currentUserPublicacionReaction[post.idPublicacion!] = null;
                return of(null);
              })
            )
        );
      } else {
        this.currentUserPublicacionReaction[post.idPublicacion!] = null;
      }

      // También cargar comentarios y sus reacciones
      postReactionObservables.push(
        this.comentarioService
          .obtenerComentariosPorPublicacion(post.idPublicacion)
          .pipe(
            map((comentarios) => {
              post.comentarios = comentarios;
              this.loadReactionsForComments(comentarios);
              return comentarios;
            }),
            catchError((err) => {
              console.error(
                `Error al cargar comentarios para la publicación ${post.idPublicacion}:`,
                err
              );
              post.comentarios = [];
              return of([]);
            })
          )
      );
    });

    if (postReactionObservables.length > 0) {
      forkJoin(postReactionObservables).subscribe({
        next: () => {
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error en forkJoin de reacciones de publicaciones:', err);
          this.cdr.detectChanges();
        },
      });
    } else {
      this.cdr.detectChanges();
    }
  }

  loadReactionsForComments(comentarios: Comentario[]): void {
    const commentReactionObservables: Observable<any>[] = [];
    const currentUser = this.authService.getUser();
    const currentUserId = currentUser ? currentUser.id : null;

    comentarios.forEach((comentario) => {
      // <-- CORRECCIÓN: Verificar idComentario antes de usarlo
      if (comentario.idComentario === undefined || comentario.idComentario === null) {
        console.warn('Comentario sin idComentario definida, saltando carga de reacciones:', comentario);
        return; // Salta este comentario si su ID no está definido
      }

      // 1. Obtener conteo de likes/dislikes para el comentario
      commentReactionObservables.push(
        this.comentarioReaccionService
          .getConteoReacciones(comentario.idComentario) // <-- CORRECCIÓN: idComentario es number
          .pipe(
            map((conteo) => {
              this.comentarioReacciones[comentario.idComentario!] = { // <-- CORRECCIÓN: Uso de non-null assertion
                like: conteo.like,
                dislike: conteo.dislike,
              };
              return conteo;
            }),
            catchError((err) => {
              console.error(
                `Error al cargar conteo de reacciones para el comentario ${comentario.idComentario}:`,
                err
              );
              this.comentarioReacciones[comentario.idComentario!] = { // <-- CORRECCIÓN: Uso de non-null assertion
                like: 0,
                dislike: 0,
              };
              return of(null);
            })
          )
      );

      // 2. Obtener la reacción del usuario actual para el comentario
      if (currentUserId) {
        commentReactionObservables.push(
          this.comentarioReaccionService
            .getReaccionUsuario(currentUserId, comentario.idComentario) // <-- CORRECCIÓN: idComentario es number
            .pipe(
              map((reaccion) => {
                this.currentUserComentarioReaction[comentario.idComentario!] = // <-- CORRECCIÓN: Uso de non-null assertion
                  reaccion;
                return reaccion;
              }),
              catchError((err) => {
                if (err.status !== 404) {
                  console.error(
                    `Error al cargar reacción de usuario para comentario ${comentario.idComentario}:`,
                    err
                  );
                }
                this.currentUserComentarioReaction[comentario.idComentario!] = // <-- CORRECCIÓN: Uso de non-null assertion
                  null;
                return of(null);
              })
            )
        );
      } else {
        this.currentUserComentarioReaction[comentario.idComentario!] = null; // <-- CORRECCIÓN: Uso de non-null assertion
      }
    });

    if (commentReactionObservables.length > 0) {
      forkJoin(commentReactionObservables).subscribe({
        next: () => {
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error en forkJoin de reacciones de comentarios:', err);
          this.cdr.detectChanges();
        },
      });
    } else {
      this.cdr.detectChanges();
    }
  }

  getConteoPublicacionLikes(idPublicacion: number): number {
    return this.publicacionReacciones[idPublicacion]?.like || 0;
  }

  getConteoPublicacionDislikes(idPublicacion: number): number {
    return this.publicacionReacciones[idPublicacion]?.dislike || 0;
  }

  togglePublicacionReaction(
    idPublicacion: number,
    tipo: TipoReaccion
  ): void {
    const currentUser = this.authService.getUser();
    if (!currentUser || !currentUser.id) {
      alert('Para reaccionar a una publicación, debes iniciar sesión.');
      return;
    }

    const currentReaction = this.currentUserPublicacionReaction[idPublicacion];
    let newTipoReaccion: TipoReaccion | null = tipo;

    if (currentReaction === tipo) {
      newTipoReaccion = null;
    }

    this.reaccionPublicacionService.toggleReaccion(
      currentUser.id,
      idPublicacion,
      newTipoReaccion
    ).subscribe({
      next: (response) => {
        this.currentUserPublicacionReaction[idPublicacion] = newTipoReaccion;
        this.reaccionPublicacionService.getConteoReacciones(idPublicacion)
          .subscribe(conteo => {
            this.publicacionReacciones[idPublicacion] = {
              like: conteo.like,
              dislike: conteo.dislike
            };
            this.cdr.detectChanges();
          });
      },
      error: (error) => {
        console.error('Error al procesar reacción de publicación:', error);
        if (error.status === 401 || error.status === 403) {
            alert('Tu sesión ha expirado o no tienes permisos. Por favor, inicia sesión de nuevo para reaccionar.');
        } else {
            alert('Ocurrió un error al procesar tu reacción. Inténtalo de nuevo.');
        }
      },
    });
  }

  hasPublicacionLiked(idPublicacion: number): boolean {
    return (
      this.currentUserPublicacionReaction[idPublicacion] === TipoReaccion.like
    );
  }

  hasPublicacionDisliked(idPublicacion: number): boolean {
    return (
      this.currentUserPublicacionReaction[idPublicacion] === TipoReaccion.dislike
    );
  }

  getConteoComentarioLikes(idComentario: number): number {
    return this.comentarioReacciones[idComentario]?.like || 0;
  }

  getConteoComentarioDislikes(idComentario: number): number {
    return this.comentarioReacciones[idComentario]?.dislike || 0;
  }

  toggleComentarioReaction(idComentario: number, tipo: TipoReaccion): void {
    const currentUser = this.authService.getUser();
    if (!currentUser || !currentUser.id) {
      alert('Para reaccionar a un comentario, debes iniciar sesión.');
      return;
    }

    const currentReaction = this.currentUserComentarioReaction[idComentario];
    let newTipoReaccion: TipoReaccion | null = tipo;

    if (currentReaction === tipo) {
      newTipoReaccion = null;
    }

    const request: ComentarioReaccionRequest = {
      idUsuario: currentUser.id,
      idComentario: idComentario,
      tipoReaccion: newTipoReaccion,
    };

    this.comentarioReaccionService.toggleReaccion(request).subscribe({
      next: (response) => {
        this.currentUserComentarioReaction[idComentario] = newTipoReaccion;
        this.comentarioReaccionService.getConteoReacciones(idComentario)
          .subscribe(conteo => {
            this.comentarioReacciones[idComentario] = {
              like: conteo.like,
              dislike: conteo.dislike
            };
            this.cdr.detectChanges();
          });
      },
      error: (error) => {
        console.error('Error al procesar reacción de comentario:', error);
        if (error.status === 401 || error.status === 403) {
            alert('Tu sesión ha expirado o no tienes permisos. Por favor, inicia sesión de nuevo para reaccionar.');
        } else {
            alert('Ocurrió un error al procesar tu reacción. Inténtalo de nuevo.');
        }
      },
    });
  }

  hasComentarioLiked(idComentario: number): boolean {
    return this.currentUserComentarioReaction[idComentario] === TipoReaccion.like;
  }

  hasComentarioDisliked(idComentario: number): boolean {
    return this.currentUserComentarioReaction[idComentario] === TipoReaccion.dislike;
  }

  addComment(publicacionId: number): void {
    const currentUser = this.authService.getUser();

    if (!currentUser || !currentUser.id) {
      alert('Para comentar, debes iniciar sesión.');
      console.error('Usuario no autenticado para enviar comentario.');
      return;
    }

    const textoComentario = this.nuevoComentarioTexto[publicacionId]?.trim();
    if (!textoComentario) {
      alert('El comentario no puede estar vacío.');
      return;
    }

    const comentarioRequest: ComentarioRequest = {
      idUsuario: currentUser.id,
      idPublicacion: publicacionId,
      texto: textoComentario,
    };

    this.comentarioService.crearComentario(comentarioRequest).subscribe({
      next: (comentarioCreado) => {
        console.log('Comentario creado:', comentarioCreado);
        const post = this.posts.find((p) => p.idPublicacion === publicacionId);
        if (post) {
          if (!post.comentarios) {
            post.comentarios = [];
          }
          post.comentarios.push(comentarioCreado);
          this.nuevoComentarioTexto[publicacionId] = '';
          this.loadReactionsForComments([comentarioCreado]);
        }
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al crear el comentario:', error);
        if (error.status === 401 || error.status === 403) {
          alert('Tu sesión ha expirado o no tienes permisos. Por favor, inicia sesión de nuevo para comentar.');
        } else {
          alert('Ocurrió un error al intentar enviar el comentario.');
        }
      },
    });
  }
}