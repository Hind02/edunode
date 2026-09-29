package com.edunode.repository;

import com.edunode.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repository = communique avec la base de données
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Récupérer les étudiants actifs
    List<Student> findByIsDeletedFalse();

    // Rechercher par filière pour les étudiants actifs
    List<Student> findByFiliereAndIsDeletedFalse(String filiere);

    // Récupérer un étudiant actif par id
    Optional<Student> findByIdAndIsDeletedFalse(Long id);

    // Calcul de la moyenne générale (sur les actifs uniquement)
    @Query("SELECT AVG(s.grade) FROM Student s WHERE s.isDeleted = false")
    Double getAverageGrade();
}
