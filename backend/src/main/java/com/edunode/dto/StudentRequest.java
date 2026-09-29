package com.edunode.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// DTO pour les requêtes POST / PUT venant du Frontend
@Data
public class StudentRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Filiere is required")
    private String filiere;

    @NotNull(message = "Grade is required")
    @DecimalMin(value = "0.0", message = "Grade must be at least 0")
    @DecimalMax(value = "20.0", message = "Grade must be at most 20")
    private Double grade;
}
