package org.example.hlcoursesappserver.dto;

import org.example.hlcoursesappserver.model.CourseCategory;

public class CourseCategoryDTO {
    private Long categoryId;
    private String categoryName;

    public CourseCategoryDTO(CourseCategory category) {
        this.categoryId = category.getCategoryId();
        this.categoryName = category.getCategoryName();
    }

    // Getters and setters
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}