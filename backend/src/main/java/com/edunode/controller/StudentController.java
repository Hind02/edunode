package com.edunode.controller;

import com.edunode.dto.StudentRequest;
import com.edunode.dto.StudentResponse;
import com.edunode.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Controller = reçoit les requêtes HTTP et appelle le Service
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    // GET étudiants (avec filtre optionnel)
    @GetMapping
    public ResponseEntity<List<StudentResponse>> getStudents(@RequestParam(required = false) String filiere) {
        if (filiere != null && !filiere.isEmpty()) {
            return ResponseEntity.ok(studentService.getStudentsByFiliere(filiere));
        }
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    // Récupérer un étudiant
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    // Ajouter
    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody StudentRequest request) {
        return new ResponseEntity<>(studentService.createStudent(request), HttpStatus.CREATED);
    }

    // Modifier
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable Long id, 
            @Valid @RequestBody StudentRequest request) {
        return ResponseEntity.ok(studentService.updateStudent(id, request));
    }

    // Supprimer logiquement
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    // Statistiques
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("averageGrade", studentService.getAverageGrade());
        // Ajout d'une stat bonus si facile
        stats.put("totalActiveStudents", studentService.getAllStudents().size()); 
        return ResponseEntity.ok(stats);
    }

    // Export CSV
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportStudentsToCsv() {
        String csvData = studentService.exportStudentsToCsv();
        byte[] output = csvData.getBytes();
        
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students.csv");
        headers.set(HttpHeaders.CONTENT_TYPE, "text/csv");
        
        return new ResponseEntity<>(output, headers, HttpStatus.OK);
    }
}
