package com.edunode.service;

import com.edunode.dto.StudentRequest;
import com.edunode.dto.StudentResponse;
import com.edunode.entity.Student;
import com.edunode.exception.StudentNotFoundException;
import com.edunode.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// Service = contient la logique métier
@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public List<StudentResponse> getAllStudents() {
        return studentRepository.findByIsDeletedFalse()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<StudentResponse> getStudentsByFiliere(String filiere) {
        return studentRepository.findByFiliereAndIsDeletedFalse(filiere)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));
        return mapToResponse(student);
    }

    public StudentResponse createStudent(StudentRequest request) {
        Student student = new Student();
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setEmail(request.getEmail());
        student.setFiliere(request.getFiliere());
        student.setGrade(request.getGrade());
        student.setIsDeleted(false);

        Student savedStudent = studentRepository.save(student);
        return mapToResponse(savedStudent);
    }

    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student student = studentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));
        
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setEmail(request.getEmail());
        student.setFiliere(request.getFiliere());
        student.setGrade(request.getGrade());

        Student updatedStudent = studentRepository.save(student);
        return mapToResponse(updatedStudent);
    }

    public void deleteStudent(Long id) {
        Student student = studentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));
        // L'annotation @SQLDelete sur l'entité transformera ce delete en UPDATE is_deleted=true
        studentRepository.delete(student);
    }

    public Double getAverageGrade() {
        Double avg = studentRepository.getAverageGrade();
        return avg != null ? avg : 0.0;
    }

    public String exportStudentsToCsv() {
        List<Student> students = studentRepository.findByIsDeletedFalse();
        StringBuilder csv = new StringBuilder();
        csv.append("ID,FirstName,LastName,Email,Filiere,Grade\n");
        for (Student s : students) {
            csv.append(s.getId()).append(",")
               .append(s.getFirstName()).append(",")
               .append(s.getLastName()).append(",")
               .append(s.getEmail()).append(",")
               .append(s.getFiliere()).append(",")
               .append(s.getGrade()).append("\n");
        }
        return csv.toString();
    }

    private StudentResponse mapToResponse(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .filiere(student.getFiliere())
                .grade(student.getGrade())
                .isDeleted(student.getIsDeleted())
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .build();
    }
}
