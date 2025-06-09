import { Component, OnInit, OnDestroy,ChangeDetectorRef  } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { SearchService } from '../../services/search.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    CommonModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatDividerModule,
    RouterLink,
    FormsModule
  ],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit, OnDestroy { 
  isLoggedIn: boolean = false;
  username: string | null = null;
  searchTerm: string = '';
  private authSubscription: Subscription = new Subscription();

  constructor(
    private authService: AuthService,
    private router: Router,
    private searchService: SearchService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.authSubscription.add(
      this.authService.isLoggedIn$.subscribe(loggedIn => {
        this.isLoggedIn = loggedIn;
        console.log('Navbar: Estado de login actualizado a:', loggedIn);
        this.cdr.detectChanges();
      })
    );

    this.authSubscription.add(
      this.authService.currentUser$.subscribe(user => {
        this.username = user ? user.username : null;
        console.log('Navbar: Nombre de usuario actualizado a:', this.username);
        this.cdr.detectChanges();
      })
    );

    this.authSubscription.add( 
      this.searchService.searchTerm$.subscribe(term => {
        this.searchTerm = term;
      })
    );
  }

  ngOnDestroy(): void {
    this.authSubscription.unsubscribe();
  }

  logout(): void {
    this.authService.logout(); 
    this.router.navigate(['/login']); 
  }

  onSearch(): void {
    this.searchService.updateSearchTerm(this.searchTerm);
    this.router.navigate(['/dashboard']);
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.searchService.updateSearchTerm('');
  }
}