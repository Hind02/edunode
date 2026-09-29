import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink],
  template: `
    <nav class="navbar">
      <div class="container navbar-container">
        <a routerLink="/" class="navbar-brand">
          <span class="logo-icon">🎓</span>
          EduNode
        </a>
        <div class="navbar-menu">
          <a routerLink="/" class="nav-link">Dashboard</a>
          <a routerLink="/student/new" class="nav-link">Add Student</a>
        </div>
      </div>
    </nav>
  `,
  styles: [`
    .navbar {
      background-color: white;
      border-bottom: 1px solid var(--border-color);
      box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
      position: sticky;
      top: 0;
      z-index: 10;
    }
    .navbar-container {
      display: flex;
      justify-content: space-between;
      align-items: center;
      height: 4rem;
    }
    .navbar-brand {
      font-size: 1.25rem;
      font-weight: 700;
      color: var(--primary-color);
      text-decoration: none;
      display: flex;
      align-items: center;
      gap: 0.5rem;
    }
    .logo-icon {
      font-size: 1.5rem;
    }
    .navbar-menu {
      display: flex;
      gap: 1.5rem;
    }
    .nav-link {
      color: var(--text-muted);
      text-decoration: none;
      font-weight: 500;
      transition: color 0.2s;
    }
    .nav-link:hover {
      color: var(--primary-color);
    }
  `]
})
export class NavbarComponent {}
