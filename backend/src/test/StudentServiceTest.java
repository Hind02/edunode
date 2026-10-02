package com.edunode.service;

import com.edunode.dto.StudentRequest;
import com.edunode.dto.StudentResponse;
import com.edunode.entity.Student;
import com.edunode.exception.StudentNotFoundException;
import com.edunode.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Test unitaire : on teste StudentService seul.
// Le repository est remplacé par un "mock" (faux objet), donc aucune base MySQL n'est nécessaire.
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student student(Long id, String first, String last, String email, String filiere, double grade) {
        Student s = new Student();
        s.setId(id);
        s.setFirstName(first);
        s.setLastName(last);
        s.setEmail(email);
        s.setFiliere(filiere);
        s.setGrade(grade);
        s.setIsDeleted(false);
        return s;
    }

    @Test
    void getStudentById_whenStudentMissing_throwsNotFound() {
        when(studentRepository.findByIdAndIsDeletedFalse(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getStudentById(99L))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createStudent_savesAnActiveStudentAndReturnsIt() {
        StudentRequest request = new StudentRequest();
        request.setFirstName("Sara");
        request.setLastName("Benali");
        request.setEmail("sara.benali@example.com");
        request.setFiliere("GI");
        request.setGrade(16.0);

        // Simule la base : elle attribue l'id 1 à l'étudiant sauvegardé
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> {
            Student saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        StudentResponse response = studentService.createStudent(request);

        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(captor.capture());
        assertThat(captor.getValue().getIsDeleted()).isFalse();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("sara.benali@example.com");
        assertThat(response.getGrade()).isEqualTo(16.0);
    }

    @Test
    void deleteStudent_whenStudentExists_callsRepositoryDelete() {
        Student existing = student(1L, "Sara", "Benali", "sara@example.com", "GI", 16.0);
        when(studentRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(existing));

        studentService.deleteStudent(1L);

        verify(studentRepository).delete(existing);
    }

    @Test
    void deleteStudent_whenStudentMissing_neverDeletesAnything() {
        when(studentRepository.findByIdAndIsDeletedFalse(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.deleteStudent(42L))
                .isInstanceOf(StudentNotFoundException.class);

        verify(studentRepository, never()).delete(any(Student.class));
    }

    @Test
    void getAverageGrade_whenNoActiveStudent_returnsZero() {
        // AVG() renvoie null en SQL quand il n'y a aucune ligne
        when(studentRepository.getAverageGrade()).thenReturn(null);

        assertThat(studentService.getAverageGrade()).isEqualTo(0.0);
    }

    @Test
    void getAverageGrade_returnsRepositoryValue() {
        when(studentRepository.getAverageGrade()).thenReturn(15.5);

        assertThat(studentService.getAverageGrade()).isEqualTo(15.5);
    }

    @Test
    void exportStudentsToCsv_containsHeaderThenOneLinePerStudent() {
        when(studentRepository.findByIsDeletedFalse()).thenReturn(List.of(
                student(1L, "Sara", "Benali", "sara@example.com", "GI", 16.0),
                student(2L, "Youssef", "Ait Omar", "youssef@example.com", "TM", 14.0)));

        String csv = studentService.exportStudentsToCsv();

        assertThat(csv).isEqualTo(
                "ID,FirstName,LastName,Email,Filiere,Grade\n"
                        + "1,Sara,Benali,sara@example.com,GI,16.0\n"
                        + "2,Youssef,Ait Omar,youssef@example.com,TM,14.0\n");
    }
}