package org.example.hlcoursesappserver.model;

import jakarta.persistence.*;
import java.util.List;

// Таблица для категорий курсов
@Entity
@Table(name = "course_categories")
public class CourseCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(nullable = false)
    private String categoryName;

    // Getters and setters

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public CourseCategory() {
    }

    public CourseCategory(String categoryName) {
        this.categoryName = categoryName;
    }
}
