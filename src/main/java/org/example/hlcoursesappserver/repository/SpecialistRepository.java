package org.example.hlcoursesappserver.repository;

import org.example.hlcoursesappserver.model.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SpecialistRepository extends JpaRepository<Specialist, Long> {
    Optional<Specialist> findByEmail(String email);
    boolean existsByEmail(String email);
}


