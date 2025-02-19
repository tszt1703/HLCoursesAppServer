package org.example.hlcoursesappserver.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    private Long specialistId;

    private Long categoryId;

    private String title;

    private String shortDescription;

    private String fullDescription;

    private String difficultyLevel;

    private String ageGroup;
    private Integer durationDays;
    private String photoUrl;

    @Column(nullable = false, columnDefinition = "VARCHAR DEFAULT 'draft'")
    private String status;
    // Getters and setters

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getSpecialistId() {
        return specialistId;
    }

    public void setSpecialistId(Long specialistId) {
        this.specialistId = specialistId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    public Course() {
    }

    public Course(Long specialistId, Long categoryId, String title, String shortDescription, String fullDescription, String difficultyLevel, String ageGroup, Integer durationDays, String photoUrl, String status) {
        this.specialistId = specialistId;
        this.categoryId = categoryId;
        this.title = title;
        this.shortDescription = shortDescription;
        this.fullDescription = fullDescription;
        this.difficultyLevel = difficultyLevel;
        this.ageGroup = ageGroup;
        this.durationDays = durationDays;
        this.photoUrl = photoUrl;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId=" + courseId +
                ", specialistId=" + specialistId +
                ", title='" + title + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", fullDescription='" + fullDescription + '\'' +
                ", difficultyLevel='" + difficultyLevel + '\'' +
                ", ageGroup='" + ageGroup + '\'' +
                ", durationDays=" + durationDays +
                ", photoUrl='" + photoUrl + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

}
