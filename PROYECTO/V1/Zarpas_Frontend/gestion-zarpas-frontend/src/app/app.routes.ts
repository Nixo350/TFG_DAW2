import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component'; 
import { SignupComponent } from './pages/signup/signup.component'; 
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { CreatePublicacionComponent } from './pages/create-publicacion/create-publicacion.component'; 
import { ProfileComponent } from './pages/user-profile/user-profile.component';
import { EditProfileComponent } from './components/edit-profile/edit-profile.component';


//Clase encargada de relacionar las ventanas de la web
export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },
  { path: 'profile', component: ProfileComponent }, 
  { path: 'dashboard', component: DashboardComponent },
  { path: 'edit-profile', component: EditProfileComponent }, 
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'create-post', component: CreatePublicacionComponent },
  { path: '**', redirectTo: 'dashboard' }
];