package com.edunode.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;

// Entity représente la table SQL
@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Utilisation d'Hibernate pour la suppression logique
// Lorsqu'on fait un delete() sur le repo, ça fait un UPDATE is_deleted=true au lieu d'un DELETE
@SQLDelete(sql = "UPDATE students SET is_deleted = true WHERE id=?")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String filiere;

    @Column(nullable = false)
    private Double grade;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
