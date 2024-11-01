package org.example.hlcoursesappserver.dto;

public class CourseDTO {
    private Long id;
    private String title;
    private String description;
    private Long specialistId;

    public CourseDTO() {}

    public CourseDTO(Long id, String title, String description, Long specialistId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.specialistId = specialistId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getSpecialistId() {
        return specialistId;
    }

    public void setSpecialistId(Long specialistId) {
        this.specialistId = specialistId;
    }
}
