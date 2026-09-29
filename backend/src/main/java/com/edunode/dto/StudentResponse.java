package com.edunode.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

// DTO représentant les données envoyées au Frontend
@Data
@Builder
public class StudentResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String filiere;
    private Double grade;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
