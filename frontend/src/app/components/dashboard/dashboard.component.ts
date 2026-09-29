import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StudentService } from '../../services/student.service';
import { StudentListComponent } from '../student-list/student-list.component';
import { Stats } from '../../models/stats';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, StudentListComponent],
  template: `
    <div class="dashboard-header mb-4">
      <div>
        <h1 class="page-title">School Management</h1>
        <p class="text-muted">Overview of your students and statistics</p>
      </div>
    </div>

    <!-- Loading State -->
    <div *ngIf="loading" class="loading-state">
      Loading dashboard...
    </div>

    <!-- Error State -->
    <div *ngIf="error" class="error-banner card mb-4">
      {{ error }}
    </div>

    <!-- Stats Cards -->
    <div class="stats-grid mb-4" *ngIf="!loading && !error && stats">
      <div class="card stat-card">
        <div class="stat-icon bg-indigo">👥</div>
        <div class="stat-details">
          <p class="stat-title">Total Active Students</p>
          <p class="stat-value">{{ stats.totalActiveStudents || 0 }}</p>
        </div>
      </div>
      
      <div class="card stat-card">
        <div class="stat-icon bg-green">📈</div>
        <div class="stat-details">
          <p class="stat-title">Average Grade</p>
          <p class="stat-value">{{ stats.averageGrade | number:'1.2-2' }}/20</p>
        </div>
      </div>
    </div>

    <!-- Student List (Child Component) -->
    <app-student-list (statsChanged)="loadStats()"></app-student-list>
  `,
  styles: [`
    .page-title {
      font-size: 1.875rem;
      font-weight: 700;
      color: var(--text-main);
      margin-bottom: 0.25rem;
    }
    .text-muted { color: var(--text-muted); }
    
    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 1.5rem;
    }
    .stat-card {
      display: flex;
      align-items: center;
      gap: 1rem;
      padding: 1.5rem;
      margin-bottom: 0;
    }
    .stat-icon {
      width: 3rem;
      height: 3rem;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 1.5rem;
    }
    .bg-indigo { background-color: #e0e7ff; color: #4338ca; }
    .bg-green { background-color: #d1fae5; color: #059669; }
    
    .stat-title {
      font-size: 0.875rem;
      color: var(--text-muted);
      font-weight: 500;
    }
    .stat-value {
      font-size: 1.5rem;
      font-weight: 700;
      color: var(--text-main);
    }
    .error-banner {
      background-color: #fee2e2;
      color: #b91c1c;
      border-left: 4px solid #ef4444;
    }
  `]
})
export class DashboardComponent implements OnInit {
  stats: Stats | null = null;
  loading = false;
  error = '';

  constructor(private studentService: StudentService) {}

  ngOnInit(): void {
    this.loadStats();
  }

  loadStats(): void {
    this.loading = true;
    this.studentService.getStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.loading = false;
        this.error = '';
      },
      error: (err) => {
        this.error = 'Failed to load statistics. Is the backend running?';
        this.loading = false;
      }
    });
  }
}
