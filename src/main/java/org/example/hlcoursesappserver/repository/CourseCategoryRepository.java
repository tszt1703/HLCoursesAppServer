package org.example.hlcoursesappserver.repository;

import org.example.hlcoursesappserver.model.CourseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseCategoryRepository extends JpaRepository<CourseCategory, Long> {
    Optional<CourseCategory> findByCategoryName(String name);

    Optional<CourseCategory> findByCategoryNameIgnoreCase(String categoryName);
}
