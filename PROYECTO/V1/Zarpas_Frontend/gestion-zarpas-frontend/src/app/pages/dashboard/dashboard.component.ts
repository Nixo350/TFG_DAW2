import { Component, OnInit, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { PostService } from '../../services/post.service';
import { Publicacion } from '../../modelos/Publicacion';
import { HttpClientModule, HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router'; 
import { ReaccionPublicacionService } from '../../services/reaccion-publicacion.service';
import { TipoReaccion } from '../../modelos/TipoReaccion';
import { AuthService } from '../../services/auth.service';
import { forkJoin, Observable, of, throwError, Subscription } from 'rxjs';
import { map, catchError, switchMap, filter, tap } from 'rxjs/operators';
import { ComentarioService } from '../../services/comentario.service';
import { ComentarioReaccionService } from '../../services/comentario-reaccion.service';
import { Comentario, ComentarioRequest } from '../../modelos/Comentario';
import { ComentarioReaccionRequest } from '../../modelos/ComentarioReaccion';
import { FormsModule } from '@angular/forms';
import { SearchService } from '../../services/search.service';
import { Categoria } from '../../modelos/Categoria';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { ReaccionPublicacionRequest } from '../../modelos/ReaccionPublicacion';
import { PublicacionGuardada,PublicacionGuardadaRequest } from '../../modelos/PublicacionGuardada'; 
import { PublicacionGuardadaService } from '../../services/publicacion-guardada.service';



@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    HttpClientModule,
    RouterLink,
    FormsModule,
    MatSnackBarModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit, OnDestroy {
  public TipoReaccion = TipoReaccion;
  posts: Publicacion[] = [];
  private subscriptions: Subscription = new Subscription();
  nuevoComentarioTexto: { [key: number]: string } = {};
  selectedCategoryId: number | null = null;
  categories: Categoria[] = [];

  constructor(
    private postService: PostService,
    private reaccionPublicacionService: ReaccionPublicacionService,
    private authService: AuthService,
    private comentarioService: ComentarioService,
    private comentarioReaccionService: ComentarioReaccionService,
    private cdr: ChangeDetectorRef,
    private searchService: SearchService,
    private snackBar: MatSnackBar,
    private publicacionGuardadaService: PublicacionGuardadaService,
    private router: Router
  ) {}

  canManagePost(post: Publicacion): boolean {
    const currentUser = this.authService.getUser();
    if (!currentUser || !post.usuario) {
      return false;
    }
    return currentUser.id === post.usuario.idUsuario;
  }

  onEditPost(post: Publicacion): void {
    if (post.idPublicacion) {
      this.router.navigate(['/editar-publicacion', post.idPublicacion]);
    }
  }

  hasCurrentUserSavedPost(post: Publicacion): boolean {
    const currentUser = this.authService.getUser();
  

  
    if (!currentUser || !currentUser.id || !post.guardadosPorUsuarios) { 
      console.log('Resultado: FALSE (falta usuario o array)');
      return false;
    }
  
    const isSaved = post.guardadosPorUsuarios.some(pg => {

      const match = pg.usuario?.idUsuario === currentUser.id;
      if (match) {
        
      }
      return match;
    });
  

    return isSaved;
  }

  ngOnInit(): void {
    this.loadPosts(); 

    this.loadCategories();

    this.subscriptions.add(
      this.searchService.searchTerm$.pipe(
        tap((term) => console.log('Dashboard: Search term updated:', term)),
        switchMap((term) => {
          if (term) {
            return this.postService.searchPublicaciones(term);
          } else {
            return this.postService.getPublicaciones();
          }
        }),
        tap(() => console.log('Dashboard: Posts reloaded due to search term change'))
      ).subscribe(
        (data) => {
          this.posts = data;
          this.loadReactionsAndCommentsForPosts(this.posts);
          
          
        },
        (error) => {
          console.error('Error al cargar publicaciones por término de búsqueda:', error);
          this.snackBar.open('Error al cargar publicaciones.', 'Cerrar', { duration: 3000 });
        }
      )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
//Clase encargada de cargar publicaciones
loadPosts(): void {
  this.subscriptions.add(
    this.postService.getPublicaciones().subscribe(
      (data: Publicacion[]) => {
        this.posts = data.map(post => ({
          ...post,
          guardadosPorUsuarios: post.guardadosPorUsuarios || [] 
        }));

        this.loadReactionsAndCommentsForPosts(this.posts);

        const currentUser = this.authService.getUser();
        if (currentUser && currentUser.id) { 
          this.subscriptions.add(
            this.publicacionGuardadaService.getPublicacionesGuardadasPorUsuario(currentUser.id) 
              .subscribe({
                next: (savedPublicationsBackend: any[]) => {
                  const savedPublicacionIds = new Set(savedPublicationsBackend.map(item => item.publicacion.idPublicacion));

                  this.posts.forEach(post => {
                    if (savedPublicacionIds.has(post.idPublicacion)) {
                      if (!post.guardadosPorUsuarios) {
                        post.guardadosPorUsuarios = [];
                      }
                      if (!post.guardadosPorUsuarios.some(pg => pg.usuario?.idUsuario === currentUser.id)) {
                        const userSavedMarker: PublicacionGuardada = {
                          usuario: { idUsuario: currentUser.id }
                        };
                        post.guardadosPorUsuarios.push(userSavedMarker);
                      }
                    } else {
                    
                      post.guardadosPorUsuarios = (post.guardadosPorUsuarios || []).filter(
                        pg => pg.usuario?.idUsuario !== currentUser.id
                      );
                    }
                  });
                  this.cdr.detectChanges(); 
                },
                error: (error) => {
                  console.error('Error al cargar las publicaciones guardadas del usuario:', error);
                  this.snackBar.open('Error al cargar estado de publicaciones guardadas.', 'Cerrar', { duration: 3000 });
                }
              })
          );
        } else {
          this.posts.forEach(post => {
            post.guardadosPorUsuarios = []; 
          });
          this.cdr.detectChanges();
        }
      },
      (error) => {
        console.error('Error al cargar publicaciones:', error);
        this.snackBar.open('Error al cargar publicaciones.', 'Cerrar', { duration: 3000 });
      }
    )
  );
}

  loadCategories(): void {
    this.subscriptions.add(
      this.postService.getAllCategorias().subscribe(
        (data) => {
          this.categories = data;
          this.cdr.detectChanges();
        },
        (error) => {
          console.error('Error al cargar categorías:', error);
          this.snackBar.open('Error al cargar categorías.', 'Cerrar', { duration: 3000 });
        }
      )
    );
  }
//Clase encargada de filtrar por categoria seleccionadas
  filterByCategory(): void {
    if (this.selectedCategoryId) {
      const selectedCategory = this.categories.find(
        (cat) => cat.idCategoria === this.selectedCategoryId
      );
      if (selectedCategory) {
        this.subscriptions.add(
          this.postService.getPublicacionesByCategoria(selectedCategory.nombre).subscribe(
            (data) => {
              this.posts = data;
              this.loadReactionsAndCommentsForPosts(this.posts);
              this.cdr.detectChanges();
            },
            (error) => {
              console.error('Error al filtrar publicaciones por categoría:', error);
              this.snackBar.open('Error al filtrar publicaciones por categoría.', 'Cerrar', { duration: 3000 });
            }
          )
        );
      }
    } else {
      this.loadPosts();
    }
  }
//Clase encargada para limpiar los filtros y recargar publicaciones
  clearCategoryFilter(): void {
    this.selectedCategoryId = null;
    this.loadPosts();
  }

  loadReactionsAndCommentsForPosts(posts: Publicacion[]): void {
    const currentUser = this.authService.getUser();
    const postDataObservables: Observable<any>[] = [];
  
    posts.forEach((post) => {
      if (!post.reaccionesPublicacion) {
        post.reaccionesPublicacion = [];
      }
      if (!post.comentarios) {
        post.comentarios = [];
      }
  
      const conteoReacciones$ = this.reaccionPublicacionService.getConteoReacciones(post.idPublicacion!).pipe(
        catchError(error => {
          console.error(`Error al cargar conteo de reacciones para publicación ${post.idPublicacion}:`, error);
          return of({ like: 0, dislike: 0 }); 
        })
      );
  
      const miReaccion$ = currentUser && currentUser.id
        ? this.reaccionPublicacionService.getReaccionUsuario(currentUser.id, post.idPublicacion!).pipe( 
            map((reaccion: any) => (reaccion ? reaccion : null)), 
            catchError(error => {
              if (error.status !== 404 && error.status !== 204) {
                  console.error(`Error al obtener miReaccion para publicación ${post.idPublicacion}:`, error);
              }
              return of(null);
            })
          )
        : of(null);
  
      this.subscriptions.add(
        this.comentarioService.obtenerComentariosPorPublicacion(post.idPublicacion!).pipe(
          tap((comentarios) => {
            post.comentarios = comentarios;
            this.loadReactionsForComments(comentarios); 
          }),
          catchError((error) => {
            console.error(`Error al cargar comentarios para la publicación ${post.idPublicacion}:`, error);
            return of([]); 
          })
        ).subscribe()
      );
  
      postDataObservables.push(
        forkJoin({
          conteo: conteoReacciones$,
          miReaccion: miReaccion$
        }).pipe(
          map(({ conteo, miReaccion }) => {
            post.conteoLikes = conteo.like;
            post.conteoDislikes = conteo.dislike;
            post.miReaccion = miReaccion;
            return post; 
          })
        )
      );
    });

    if (postDataObservables.length > 0) {
      this.subscriptions.add(
        forkJoin(postDataObservables).subscribe({
          next: () => {
            this.cdr.detectChanges();
          },
          error: (error) => {
            console.error('Error al cargar reacciones y comentarios para publicaciones:', error);
            this.snackBar.open('Error al cargar detalles de publicaciones.', 'Cerrar', { duration: 3000 });
          }
        })
      );
    } else {
      this.cdr.detectChanges();
    }
  }


  loadReactionsForComments(comments: Comentario[]): void {
    const currentUser = this.authService.getUser();
    if (!currentUser || !currentUser.id || !comments || comments.length === 0) {
      return;
    }

    const commentDataObservables: Observable<any>[] = comments.map(comment => {
      const miReaccionComentario$ = this.comentarioReaccionService.getReaccionByComentarioAndUsuario(
        comment.idComentario!,
        currentUser.id
      ).pipe(
        map((reaccion: any) => (reaccion ? reaccion : null)), 
        catchError(error => {
          if (error.status !== 404 && error.status !== 204) {
              console.error(`Error al cargar miReaccion para comentario ${comment.idComentario}:`, error);
          }
          return of(null); 
        })
      );

      const conteoComentario$ = this.comentarioReaccionService.getConteoReacciones(comment.idComentario!).pipe(
        catchError(error => {
          console.error(`Error al cargar conteo de reacciones para comentario ${comment.idComentario}:`, error);
          return of({ like: 0, dislike: 0 }); 
        })
      );

      return forkJoin({
        miReaccion: miReaccionComentario$,
        conteo: conteoComentario$
      }).pipe(
        map(({ miReaccion, conteo }) => {
          return {
            idComentario: comment.idComentario,
            miReaccion: miReaccion,
            conteoLikes: conteo.like || 0,
            conteoDislikes: conteo.dislike || 0
          };
        })
      );
    });

    this.subscriptions.add(
      forkJoin(commentDataObservables).subscribe({
        next: (results) => {
          results.forEach(res => {
            const comment = comments.find(c => c.idComentario === res.idComentario);
            if (comment) {
              comment.currentUserReaction = res.miReaccion;
              comment.conteoLikes = res.conteoLikes;    
              comment.conteoDislikes = res.conteoDislikes; 
            }
          });
          this.cdr.detectChanges(); 
        },
        error: (error) => {
          console.error('Error al cargar reacciones y conteos para comentarios:', error);
          this.snackBar.open('Error al cargar detalles de comentarios.', 'Cerrar', { duration: 3000 });
        }
      })
    );
  }

  onLike(publicacion: Publicacion): void {
    this.reactToPost(publicacion, TipoReaccion.like);
  }

  onDislike(publicacion: Publicacion): void {
    this.reactToPost(publicacion, TipoReaccion.dislike);
  }
//Clase encargada de reaccionar a las publicaciones
  reactToPost(publicacion: Publicacion, tipo: TipoReaccion): void {
    const currentUser = this.authService.getUser();
    if (!currentUser || !currentUser.id) {
      this.snackBar.open('Para reaccionar, debes iniciar sesión.', 'Cerrar', { duration: 3000 });
      return;
    }

    this.subscriptions.add(
      this.reaccionPublicacionService.reaccionar({
        idPublicacion: publicacion.idPublicacion,
        idUsuario: currentUser.id,
        tipoReaccion: tipo
      }).pipe(
        switchMap(() =>
          forkJoin({

            conteo: this.reaccionPublicacionService.getConteoReacciones(publicacion.idPublicacion!),
   
            miReaccion: this.reaccionPublicacionService.getReaccionUsuario(currentUser.id!, publicacion.idPublicacion!).pipe(
              map((reaccion: any) => (reaccion ? reaccion : null)),
              catchError(error => {
                if (error.status !== 404 && error.status !== 204) { console.error('Error fetching user reaction after toggle:', error); }
                return of(null);
              })
            )
          })
        ),
        tap(({ conteo, miReaccion }) => {
          publicacion.conteoLikes = conteo.like || 0;
          publicacion.conteoDislikes = conteo.dislike || 0;
          publicacion.miReaccion = miReaccion;
          this.cdr.detectChanges();
        }),
        catchError(error => {
          console.error('Error al reaccionar a la publicación:', error);
          this.snackBar.open('Error al reaccionar a la publicación.', 'Cerrar', { duration: 3000 });
          return throwError(() => new Error('Error al reaccionar a la publicación.'));
        })
      ).subscribe()
    );
  }

//Clase encargada de guardar las publicaciones
  onSaveToggle(post: Publicacion): void {
    const idPublicacion = post.idPublicacion;
    const currentUser = this.authService.getUser();
  
    if (!currentUser || !currentUser.id) {
      this.snackBar.open('Debes iniciar sesión para guardar publicaciones.', 'Cerrar', { duration: 3000 });
      return;
    }
  
    const isSavedCurrently = this.hasCurrentUserSavedPost(post);
  
    if (isSavedCurrently) {

      this.publicacionGuardadaService.unsavePublicacion(idPublicacion).subscribe({
        next: () => {
          console.log('Publicación desguardada exitosamente.');
          this.snackBar.open('Publicación desguardada.', 'Cerrar', { duration: 3000 });

          post.guardadosPorUsuarios = (post.guardadosPorUsuarios || []).filter(
            pg => pg.usuario?.idUsuario !== currentUser.id
          );
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('Error al desguardar publicación:', error);
          this.snackBar.open('Error al desguardar publicación.', 'Cerrar', { duration: 3000 });
        }
      });
    } else {

      this.publicacionGuardadaService.savePublicacion(idPublicacion).subscribe({
        next: () => {
          console.log('Publicación guardada exitosamente.');
          this.snackBar.open('Publicación guardada.', 'Cerrar', { duration: 3000 });
  
 
          if (!post.guardadosPorUsuarios) {
              post.guardadosPorUsuarios = [];
          }
          const newPublicacionGuardada: PublicacionGuardada = {
            usuario: { idUsuario: currentUser.id }
          };
          if (!(post.guardadosPorUsuarios || []).some(pg => pg.usuario?.idUsuario === currentUser.id)) { 
              (post.guardadosPorUsuarios || []).push(newPublicacionGuardada); 
          }
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('Error al guardar publicación:', error);
          this.snackBar.open('Error al guardar publicación.', 'Cerrar', { duration: 3000 });
        }
      });
    }
  }

  sendComment(publicacionId: number): void {
    const currentUser = this.authService.getUser();
    if (!currentUser || !currentUser.id) {
      this.snackBar.open('Para comentar, debes iniciar sesión.', 'Cerrar', { duration: 3000 });
      console.error('Usuario no autenticado para enviar comentario.');
      return;
    }

    const textoComentario = this.nuevoComentarioTexto[publicacionId]?.trim();
    if (!textoComentario) {
      this.snackBar.open('El comentario no puede estar vacío.', 'Cerrar', { duration: 3000 });
      return;
    }

    const comentarioRequest: ComentarioRequest = {
      idUsuario: currentUser.id,
      idPublicacion: publicacionId,
      texto: textoComentario,
    };

    this.subscriptions.add(
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
          this.snackBar.open('Comentario añadido.', 'Cerrar', { duration: 3000 });
        },
        error: (error) => {
          console.error('Error al crear el comentario:', error);
          if (error.status === 401 || error.status === 403) {
            this.snackBar.open('Tu sesión ha expirado o no tienes permisos. Por favor, inicia sesión de nuevo.', 'Cerrar', { duration: 5000 });
          } else {
            this.snackBar.open('Error al añadir comentario: ' + (error.error?.message || 'Error desconocido.'), 'Cerrar', { duration: 3000 });
          }
        },
      })
    );
  }

  onDeletePost(idPublicacion: number): void {
    if (
      confirm(
        '¿Estás seguro de que quieres eliminar esta publicación? Esta acción no se puede deshacer.'
      )
    ) {
      this.subscriptions.add(
        this.postService.deletePublicacion(idPublicacion).subscribe({
          next: () => {
            console.log(`Publicación ${idPublicacion} eliminada correctamente.`);
            this.posts = this.posts.filter((p) => p.idPublicacion !== idPublicacion);
            this.snackBar.open('Publicación eliminada correctamente.', 'Cerrar', {
              duration: 3000,
            });
            this.cdr.detectChanges();
          },
          error: (error: HttpErrorResponse) => {
            console.error('Error al eliminar la publicación:', error);
            if (error.status === 401 || error.status === 403) {
              this.snackBar.open(
                'No tienes permisos para eliminar esta publicación.',
                'Cerrar',
                { duration: 5000 }
              );
            } else {
              this.snackBar.open(
                'Error al eliminar la publicación: ' + (error.error?.message || 'Error desconocido.'),
                'Cerrar',
                { duration: 3000 }
              );
            }
          },
        })
      );
    }
  }



  reaccionarComentario(idComentario: number, tipo: TipoReaccion): void {
    const userFromAuth = this.authService.getUser();
    const idUsuario = userFromAuth?.id;

    if (!idUsuario) {
      this.snackBar.open('Debes iniciar sesión para reaccionar a comentarios.', 'Cerrar', { duration: 3000 });
      return;
    }

    const comentarioReaccionRequest: ComentarioReaccionRequest = {
      idComentario: idComentario,
      idUsuario: idUsuario,
      tipoReaccion: tipo
    };

    this.subscriptions.add(
      this.comentarioReaccionService.reaccionar(comentarioReaccionRequest).pipe(

        switchMap(() =>
          forkJoin({
            conteo: this.comentarioReaccionService.getConteoReacciones(idComentario),
            miReaccion: this.comentarioReaccionService.getReaccionByComentarioAndUsuario(idComentario, idUsuario).pipe(
              map((reaccion: any) => (reaccion ? reaccion.tipoReaccion : null)),
              catchError(error => {
                if (error.status !== 404 && error.status !== 204) { console.error('Error fetching user comment reaction:', error); }
                return of(null);
              })
            )
          })
        ),
        tap(({ conteo, miReaccion }) => {
          let found = false;
          for (const post of this.posts) {
            if (post.comentarios) {
              const comentario = post.comentarios.find(c => c.idComentario === idComentario);
              if (comentario) {
                comentario.conteoLikes = conteo.like || 0;
                comentario.conteoDislikes = conteo.dislike || 0;
                comentario.currentUserReaction = miReaccion;
                found = true;
                break;
              }
            }
          }
          if (found) {
            this.cdr.detectChanges();
          }
        }),
        catchError((error: HttpErrorResponse) => {
          console.error('Error al reaccionar al comentario:', error);
          this.snackBar.open('Error al reaccionar al comentario.', 'Cerrar', { duration: 3000 });
          return throwError(() => new Error('Error al reaccionar al comentario.'));
        })
      ).subscribe()
    );
  }
}