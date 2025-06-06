import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http'; // <-- ¡IMPORTANTE! Para HttpClient
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async'; // <-- ¡IMPORTANTE! Para animaciones de Material

import { routes } from './app.routes'; // Asegúrate de que esta ruta sea correcta para tus rutas

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(), // Habilita el módulo HTTP
    provideAnimationsAsync() // Habilita las animaciones de Material Design
  ]
};