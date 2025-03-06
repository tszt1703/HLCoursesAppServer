package org.example.hlcoursesappserver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public class CourseRequest {

    @NotNull(message = "Идентификатор специалиста обязателен")
    private Long specialistId;

    @Size(max = 255, message = "Название категории не должно превышать 255 символов")
    private String categoryName; // Заменяем categoryId на categoryName

    @Size(max = 255, message = "Название курса не должно превышать 255 символов")
    private String title;

    private String shortDescription;

    private String fullDescription;

    private String difficultyLevel;

    // Поле ageGroup можно оставить опциональным
    private String ageGroup;

    // Поле длительности курса (в днях) можно оставить опциональным
    private Integer durationDays;

    //@URL(message = "Некорректный URL для фото")
    private String photoUrl;

    public CourseRequest() {
    }

    // Геттеры и сеттеры
    public Long getSpecialistId() {
        return specialistId;
    }

    public void setSpecialistId(Long specialistId) {
        this.specialistId = specialistId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}