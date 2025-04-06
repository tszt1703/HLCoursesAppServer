package org.example.hlcoursesappserver.dto;

import org.example.hlcoursesappserver.model.CourseModule;

public class CourseModuleDTO {
    private Long moduleId;
    private String title;
    private String description;
    private Integer position;

    public CourseModuleDTO(CourseModule module) {
        this.moduleId = module.getModuleId();
        this.title = module.getTitle();
        this.description = module.getDescription();
        this.position = module.getPosition();
    }

    // Геттеры и сеттеры
    public Long getModuleId() { return moduleId; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }
}
