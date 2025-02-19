package org.example.hlcoursesappserver.repository;

import org.example.hlcoursesappserver.model.CourseModule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseModuleRepository extends JpaRepository<CourseModule, Long> {
}
