package org.example.hlcoursesappserver.dto;

import org.example.hlcoursesappserver.model.Lesson;

public class LessonDTO {
    private Long lessonId;
    private Long moduleId;
    private String title;
    private String content;
    private String photoUrl;
    private String videoUrl;
    private Integer position;

    public LessonDTO(Lesson lesson) {
        this.lessonId = lesson.getLessonId();
        this.moduleId = lesson.getModuleId();
        this.title = lesson.getTitle();
        this.content = lesson.getContent();
        this.photoUrl = lesson.getPhotoUrl();
        this.videoUrl = lesson.getVideoUrl();
        this.position = lesson.getPosition();
    }

    // Getters and setters
    public Long getLessonId() { return lessonId; }
    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }
    public Long getModuleId() { return moduleId; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }
}
