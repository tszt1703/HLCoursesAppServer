package org.example.hlcoursesappserver.repository;

import org.example.hlcoursesappserver.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<Test, Long> {
}
