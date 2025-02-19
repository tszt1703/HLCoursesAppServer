package org.example.hlcoursesappserver.repository;

import org.example.hlcoursesappserver.model.CourseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseCategoryRepository extends JpaRepository<CourseCategory, Long> {
}
