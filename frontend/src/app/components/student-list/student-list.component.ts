import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { StudentService } from '../../services/student.service';
import { Student } from '../../models/student';

@Component({
  selector: 'app-student-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  template: `
    <div class="card">
      <div class="header-actions d-flex justify-between align-center mb-4">
        <h2 class="card-title">Students List</h2>
        
        <div class="actions d-flex gap-2">
          <!-- Filtre -->
          <div class="filter-group">
            <input 
              type="text" 
              class="form-control" 
              placeholder="Filter by Filiere (ex: GI)" 
              [(ngModel)]="filterFiliere"
              (keyup.enter)="applyFilter()"
            >
            <button class="btn btn-secondary" (click)="applyFilter()">Filter</button>
            <button class="btn btn-secondary" (click)="clearFilter()" *ngIf="filterFiliere">Clear</button>
          </div>
          
          <button class="btn btn-secondary" (click)="exportCsv()">
            <span class="icon">📥</span> Export CSV
          </button>
          <a routerLink="/student/new" class="btn btn-primary">
            <span class="icon">➕</span> Add Student
          </a>
        </div>
      </div>

      <!-- Table -->
      <div class="table-container">
        <table *ngIf="students.length > 0; else emptyState">
          <thead>
            <tr>
              <th>Name</th>
              <th>Email</th>
              <th>Filiere</th>
              <th>Grade</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let student of students">
              <td>
                <div class="font-medium">{{ student.firstName }} {{ student.lastName }}</div>
              </td>
              <td class="text-muted">{{ student.email }}</td>
              <td>
                <span class="badge">{{ student.filiere }}</span>
              </td>
              <td>
                <div class="grade-display" [class.excellent]="student.grade >= 16" [class.good]="student.grade >= 12 && student.grade < 16">
                  {{ student.grade | number:'1.2-2' }}
                </div>
              </td>
              <td>
                <div class="d-flex gap-2">
                  <a [routerLink]="['/student/edit', student.id]" class="action-btn edit" title="Edit">✏️</a>
                  <button class="action-btn delete" (click)="deleteStudent(student)" title="Delete">🗑️</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Empty State -->
      <ng-template #emptyState>
        <div class="empty-state text-center">
          <div class="empty-icon">📭</div>
          <h3>No students found</h3>
          <p class="text-muted">Get started by adding a new student or clear your filters.</p>
        </div>
      </ng-template>
    </div>
  `,
  styles: [`
    .card-title {
      font-size: 1.25rem;
      font-weight: 600;
    }
    .header-actions { flex-wrap: wrap; gap: 1rem; }
    .filter-group { display: flex; gap: 0.5rem; }
    .filter-group input { width: 200px; }
    
    .font-medium { font-weight: 500; }
    
    .grade-display { font-weight: 600; }
    .excellent { color: #059669; }
    .good { color: #2563eb; }
    
    .action-btn {
      background: none;
      border: none;
      cursor: pointer;
      font-size: 1rem;
      opacity: 0.7;
      transition: opacity 0.2s;
    }
    .action-btn:hover { opacity: 1; }
    .action-btn.edit:hover { filter: drop-shadow(0 0 2px blue); }
    .action-btn.delete:hover { filter: drop-shadow(0 0 2px red); }
    
    .empty-state {
      padding: 3rem 1rem;
    }
    .empty-icon {
      font-size: 3rem;
      margin-bottom: 1rem;
    }
  `]
})
export class StudentListComponent implements OnInit {
  students: Student[] = [];
  filterFiliere: string = '';
  
  @Output() statsChanged = new EventEmitter<void>();

  constructor(private studentService: StudentService) {}

  ngOnInit(): void {
    this.loadStudents();
  }

  loadStudents(): void {
    this.studentService.getStudents().subscribe(data => {
      this.students = data;
    });
  }

  applyFilter(): void {
    if (this.filterFiliere.trim()) {
      this.studentService.getStudentsByFiliere(this.filterFiliere.trim()).subscribe(data => {
        this.students = data;
      });
    } else {
      this.loadStudents();
    }
  }

  clearFilter(): void {
    this.filterFiliere = '';
    this.loadStudents();
  }

  deleteStudent(student: Student): void {
    if (confirm(`Are you sure you want to delete ${student.firstName} ${student.lastName}?`)) {
      this.studentService.deleteStudent(student.id!).subscribe(() => {
        this.loadStudents();
        this.statsChanged.emit();
      });
    }
  }

  exportCsv(): void {
    this.studentService.exportStudents();
  }
}
