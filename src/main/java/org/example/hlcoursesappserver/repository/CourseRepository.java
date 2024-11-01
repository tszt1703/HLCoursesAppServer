package org.example.hlcoursesappserver.repository;

import org.example.hlcoursesappserver.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
