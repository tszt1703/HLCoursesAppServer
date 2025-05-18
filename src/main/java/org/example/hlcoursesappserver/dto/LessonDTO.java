package org.example.hlcoursesappserver.dto;

import org.example.hlcoursesappserver.model.Lesson;
import org.example.hlcoursesappserver.model.LessonFile;

import java.util.List;
import java.util.stream.Collectors;

public class LessonDTO {
    private Long lessonId;
    private Long moduleId;
    private String title;
    private String content;
    private Integer position;
    private List<String> fileUrls;

    public LessonDTO(Lesson lesson) {
        this.lessonId = lesson.getLessonId();
        this.moduleId = lesson.getModuleId();
        this.title = lesson.getTitle();
        this.content = lesson.getContent();
        this.position = lesson.getPosition();
        this.fileUrls = lesson.getFiles().stream()
                .map(LessonFile::getFileUrl)
                .collect(Collectors.toList());
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
    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }
    public List<String> getFileUrls() { return fileUrls; }
    public void setFileUrls(List<String> fileUrls) { this.fileUrls = fileUrls; }
}