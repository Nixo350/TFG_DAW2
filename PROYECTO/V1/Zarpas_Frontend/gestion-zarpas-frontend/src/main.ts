import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config'; // Importa tu configuración completa
import { AppComponent } from './app/app.component';

bootstrapApplication(AppComponent, appConfig) // Pasa appConfig directamente aquí
  .catch(err => console.error(err));