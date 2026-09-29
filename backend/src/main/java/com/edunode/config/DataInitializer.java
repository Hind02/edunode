package com.edunode.config;

import com.edunode.entity.Student;
import com.edunode.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

// Insérer des données initiales uniquement si la table est vide
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final StudentRepository studentRepository;

    @Override
    public void run(String... args) throws Exception {
        if (studentRepository.count() == 0) {
            Student s1 = new Student();
            s1.setFirstName("Sara");
            s1.setLastName("Benali");
            s1.setEmail("sara.benali@example.com");
            s1.setFiliere("GI");
            s1.setGrade(16.0);
            s1.setIsDeleted(false);

            Student s2 = new Student();
            s2.setFirstName("Youssef");
            s2.setLastName("Ait Omar");
            s2.setEmail("youssef.aitomar@example.com");
            s2.setFiliere("TM");
            s2.setGrade(14.0);
            s2.setIsDeleted(false);

            Student s3 = new Student();
            s3.setFirstName("Nadia");
            s3.setLastName("El Fassi");
            s3.setEmail("nadia.elfassi@example.com");
            s3.setFiliere("GI");
            s3.setGrade(18.0);
            s3.setIsDeleted(false);

            studentRepository.saveAll(Arrays.asList(s1, s2, s3));
            System.out.println("Données initiales insérées avec succès.");
        }
    }
}
