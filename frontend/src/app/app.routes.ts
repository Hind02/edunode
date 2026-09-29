import { Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { StudentFormComponent } from './components/student-form/student-form.component';

export const routes: Routes = [
  { path: '', component: DashboardComponent },
  { path: 'student/new', component: StudentFormComponent },
  { path: 'student/edit/:id', component: StudentFormComponent },
  { path: '**', redirectTo: '' }
];
