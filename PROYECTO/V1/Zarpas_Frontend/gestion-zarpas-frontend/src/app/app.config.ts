// src/app/app.config.ts

import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http'; // <-- ¡IMPORTANTE! Añade 'withInterceptorsFromDi'
import { HTTP_INTERCEPTORS } from '@angular/common/http'; // <-- ¡IMPORTANTE! Importa HTTP_INTERCEPTORS
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';

import { routes } from './app.routes'; // Asegúrate de que esta ruta sea correcta para tus rutas
import { AuthInterceptor } from './interceptors/auth.interceptor'; // <-- ¡IMPORTANTE! Importa tu interceptor

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    // Habilita el módulo HTTP y permite el uso de interceptores basados en DI
    provideHttpClient(withInterceptorsFromDi()), // <-- ¡CAMBIO AQUÍ!
    
    // Registra tu Interceptor HTTP
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true // Es crucial si vas a tener más de un interceptor
    },

    provideAnimationsAsync() // Habilita las animaciones de Material Design
    
    // Aquí puedes añadir otros servicios que sean `providedIn: 'root'` si lo deseas,
    // pero no es estrictamente necesario si ya tienen `providedIn: 'root'`.
    // Ejemplo: AuthService, PostService, ComentarioService, SearchService
  ]
};