import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { StudentService } from '../../services/student.service';
import { Student } from '../../models/student';

@Component({
  selector: 'app-student-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="form-container card mx-auto" style="max-width: 600px;">
      <h2 class="mb-4">{{ isEditMode ? 'Update Student' : 'Add New Student' }}</h2>
      
      <!-- Messages -->
      <div *ngIf="successMessage" class="alert alert-success mb-4">
        {{ successMessage }}
      </div>
      <div *ngIf="errorMessage" class="alert alert-error mb-4">
        {{ errorMessage }}
      </div>

      <form [formGroup]="studentForm" (ngSubmit)="onSubmit()">
        <div class="d-flex gap-4 mb-4">
          <div class="form-group flex-1">
            <label class="form-label">First Name</label>
            <input type="text" class="form-control" formControlName="firstName" placeholder="Sara">
            <div *ngIf="isInvalid('firstName')" class="error-text">First name is required.</div>
          </div>
          
          <div class="form-group flex-1">
            <label class="form-label">Last Name</label>
            <input type="text" class="form-control" formControlName="lastName" placeholder="Benali">
            <div *ngIf="isInvalid('lastName')" class="error-text">Last name is required.</div>
          </div>
        </div>

        <div class="form-group mb-4">
          <label class="form-label">Email</label>
          <input type="email" class="form-control" formControlName="email" placeholder="sara.benali@example.com">
          <div *ngIf="isInvalid('email')" class="error-text">A valid email is required.</div>
        </div>

        <div class="d-flex gap-4 mb-4">
          <div class="form-group flex-1">
            <label class="form-label">Filiere</label>
            <input type="text" class="form-control" formControlName="filiere" placeholder="GI">
            <div *ngIf="isInvalid('filiere')" class="error-text">Filiere is required.</div>
          </div>

          <div class="form-group flex-1">
            <label class="form-label">Grade (0 - 20)</label>
            <input type="number" class="form-control" formControlName="grade" step="0.25" min="0" max="20">
            <div *ngIf="isInvalid('grade')" class="error-text">Grade must be between 0 and 20.</div>
          </div>
        </div>

        <div class="form-actions d-flex justify-between mt-4 pt-4 border-top">
          <a routerLink="/" class="btn btn-secondary">Cancel</a>
          <button type="submit" class="btn btn-primary" [disabled]="loading || studentForm.invalid">
            {{ loading ? 'Saving...' : (isEditMode ? 'Update Student' : 'Add Student') }}
          </button>
        </div>
      </form>
    </div>
  `,
  styles: [`
    .flex-1 { flex: 1; }
    .mx-auto { margin-left: auto; margin-right: auto; }
    .mt-4 { margin-top: 1rem; }
    .pt-4 { padding-top: 1rem; }
    .border-top { border-top: 1px solid var(--border-color); }
    
    .alert {
      padding: 0.75rem 1rem;
      border-radius: 6px;
      font-size: 0.875rem;
    }
    .alert-success {
      background-color: #d1fae5;
      color: #065f46;
      border: 1px solid #10b981;
    }
    .alert-error {
      background-color: #fee2e2;
      color: #991b1b;
      border: 1px solid #ef4444;
    }
  `]
})
export class StudentFormComponent implements OnInit {
  studentForm!: FormGroup;
  isEditMode = false;
  studentId?: number;
  loading = false;
  
  successMessage = '';
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private studentService: StudentService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.initForm();
    
    // Check if we are in edit mode
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.studentId = +params['id'];
        this.loadStudent(this.studentId);
      }
    });
  }

  initForm(): void {
    this.studentForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      filiere: ['', Validators.required],
      grade: ['', [Validators.required, Validators.min(0), Validators.max(20)]]
    });
  }

  loadStudent(id: number): void {
    this.studentService.getStudentById(id).subscribe({
      next: (student) => {
        this.studentForm.patchValue({
          firstName: student.firstName,
          lastName: student.lastName,
          email: student.email,
          filiere: student.filiere,
          grade: student.grade
        });
      },
      error: () => {
        this.errorMessage = 'Student not found.';
      }
    });
  }

  isInvalid(controlName: string): boolean {
    const control = this.studentForm.get(controlName);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  onSubmit(): void {
    if (this.studentForm.invalid) {
      this.studentForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.successMessage = '';
    this.errorMessage = '';

    const studentData: Student = this.studentForm.value;

    if (this.isEditMode && this.studentId) {
      this.studentService.updateStudent(this.studentId, studentData).subscribe({
        next: () => {
          this.successMessage = 'Student updated successfully!';
          this.loading = false;
          setTimeout(() => this.router.navigate(['/']), 1500);
        },
        error: (err) => {
          this.handleError(err);
        }
      });
    } else {
      this.studentService.createStudent(studentData).subscribe({
        next: () => {
          this.successMessage = 'Student added successfully!';
          this.studentForm.reset();
          this.loading = false;
          setTimeout(() => this.router.navigate(['/']), 1500);
        },
        error: (err) => {
          this.handleError(err);
        }
      });
    }
  }

  private handleError(err: any): void {
    this.loading = false;
    if (err.status === 401) {
      this.errorMessage = 'Unauthorized: API Key is invalid or missing.';
    } else {
      this.errorMessage = 'An error occurred while saving the student.';
    }
  }
}
