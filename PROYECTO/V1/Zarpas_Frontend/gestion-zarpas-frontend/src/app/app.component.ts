import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router'; // Necesario para <router-outlet>
import { MatButtonModule } from '@angular/material/button'; // Si lo usas directamente en app.component.html
import { NavbarComponent } from "./components/navbar/navbar.component"; // Si lo usas directamente en app.component.html


@Component({
  selector: 'app-root',
  standalone: true, // Si tu app.component es standalone, asegúrate de que esté
  imports: [
    RouterOutlet,
    MatButtonModule, // Mantén solo los módulos que uses directamente en app.component.html
    NavbarComponent  // Mantén solo los componentes que uses directamente en app.component.html (como la barra de navegación)
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'zarpas';
}