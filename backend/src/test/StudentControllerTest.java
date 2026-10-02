package com.edunode.controller;

import com.edunode.dto.StudentResponse;
import com.edunode.exception.StudentNotFoundException;
import com.edunode.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Test de la couche web uniquement : Spring démarre le contrôleur, l'intercepteur de clé API
// et le gestionnaire d'erreurs, mais PAS la base de données. Le service est simulé (mock).
@WebMvcTest(StudentController.class)
class StudentControllerTest {

        private static final String API_KEY = "edunode-admin-key";

        private static final String VALID_BODY = """
                        {
                          "firstName": "Sara",
                          "lastName": "Benali",
                          "email": "sara.benali@example.com",
                          "filiere": "GI",
                          "grade": 16.0
                        }
                        """;

        private static final String INVALID_BODY = """
                        {
                          "firstName": "",
                          "lastName": "Benali",
                          "email": "pas-un-email",
                          "filiere": "GI",
                          "grade": 25.0
                        }
                        """;

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private StudentService studentService;

        @Test
        void getStudents_doesNotRequireApiKey() throws Exception {
                when(studentService.getAllStudents()).thenReturn(List.of());

                mockMvc.perform(get("/api/students"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray());
        }

        @Test
        void createStudent_withoutApiKey_returns401() throws Exception {
                mockMvc.perform(post("/api/students")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(VALID_BODY))
                                .andExpect(status().isUnauthorized());

                verifyNoInteractions(studentService);
        }

        @Test
        void createStudent_withWrongApiKey_returns401() throws Exception {
                mockMvc.perform(post("/api/students")
                                .header("X-API-KEY", "mauvaise-cle")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(VALID_BODY))
                                .andExpect(status().isUnauthorized());

                verifyNoInteractions(studentService);
        }

        @Test
        void createStudent_withValidKeyAndBody_returns201() throws Exception {
                StudentResponse created = StudentResponse.builder()
                                .id(1L)
                                .firstName("Sara")
                                .lastName("Benali")
                                .email("sara.benali@example.com")
                                .filiere("GI")
                                .grade(16.0)
                                .isDeleted(false)
                                .build();
                when(studentService.createStudent(any())).thenReturn(created);

                mockMvc.perform(post("/api/students")
                                .header("X-API-KEY", API_KEY)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(VALID_BODY))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.email").value("sara.benali@example.com"));
        }

        @Test
        void createStudent_withInvalidBody_returns400AndFieldErrors() throws Exception {
                mockMvc.perform(post("/api/students")
                                .header("X-API-KEY", API_KEY)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(INVALID_BODY))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.firstName").exists())
                                .andExpect(jsonPath("$.email").exists())
                                .andExpect(jsonPath("$.grade").exists());

                verifyNoInteractions(studentService);
        }

        @Test
        void getStudentById_whenUnknown_returns404WithMessage() throws Exception {
                when(studentService.getStudentById(99L))
                                .thenThrow(new StudentNotFoundException("Student not found with id: 99"));

                mockMvc.perform(get("/api/students/99"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Student not found with id: 99"));
        }

        @Test
        void deleteStudent_withoutApiKey_returns401() throws Exception {
                mockMvc.perform(delete("/api/students/1"))
                                .andExpect(status().isUnauthorized());

                verifyNoInteractions(studentService);
        }
}