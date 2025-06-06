import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component'; // Asegúrate de que esta ruta sea correcta
import { SignupComponent } from './pages/signup/signup.component'; // Asegúrate de que esta ruta sea correcta
import { DashboardComponent } from './pages/dashboard/dashboard.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }
];