package org.example.hlcoursesappserver.dto;

import org.example.hlcoursesappserver.model.Course;
import org.example.hlcoursesappserver.model.CourseModule;

import java.util.List;
import java.util.stream.Collectors;

public class CourseDTO {
    private Long courseId;
    private Long specialistId;
    // private List<Long> categoryIds; // Список ID категорий
    private List<String> categoryNames; // Список названий категорий
    private String title;
    private String shortDescription;
    private String fullDescription;
    private String difficultyLevel;
    private String ageGroup;
    private Integer durationDays;
    private String photoUrl;
    private String status;
    private List<CourseModuleDTO> modules;

    // Конструктор для маппинга из Course
    public CourseDTO(Course course) {
        this.courseId = course.getCourseId();
        this.specialistId = course.getSpecialistId();
        //this.categoryIds = course.getCategories().stream()
               // .map(category -> category.getCategoryId())
                //.collect(Collectors.toList());
        this.categoryNames = course.getCategories().stream()
                .map(category -> category.getCategoryName())
                .collect(Collectors.toList());
        this.title = course.getTitle();
        this.shortDescription = course.getShortDescription();
        this.fullDescription = course.getFullDescription();
        this.difficultyLevel = course.getDifficultyLevel();
        this.ageGroup = course.getAgeGroup();
        this.durationDays = course.getDurationDays();
        this.photoUrl = course.getPhotoUrl();
        this.status = course.getStatus();
        if (course.getModules() != null) {
            this.modules = course.getModules().stream()
                    .map(CourseModuleDTO::new)
                    .collect(Collectors.toList());
        }
    }

    // Геттеры и сеттеры
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Long getSpecialistId() { return specialistId; }
    public void setSpecialistId(Long specialistId) { this.specialistId = specialistId; }
  //  public List<Long> getCategoryIds() { return categoryIds; }
  //  public void setCategoryIds(List<Long> categoryIds) { this.categoryIds = categoryIds; }
    public List<String> getCategoryNames() { return categoryNames; }
    public void setCategoryNames(List<String> categoryNames) { this.categoryNames = categoryNames; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
    public String getFullDescription() { return fullDescription; }
    public void setFullDescription(String fullDescription) { this.fullDescription = fullDescription; }
    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    public String getAgeGroup() { return ageGroup; }
    public void setAgeGroup(String ageGroup) { this.ageGroup = ageGroup; }
    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<CourseModuleDTO> getModules() { return modules; }
    public void setModules(List<CourseModuleDTO> modules) { this.modules = modules; }
}