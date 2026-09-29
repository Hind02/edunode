export interface Student {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  filiere: string;
  grade: number;
  isDeleted?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
