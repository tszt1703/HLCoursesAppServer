package org.example.hlcoursesappserver.model;

import jakarta.persistence.*;

@Entity
@Table(name = "lesson_files")
public class LessonFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    private String fileType; // Тип файла: 'photo', 'video', 'document'

    private String fileName; // Название файла

    private String fileUrl; // URL файла

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private java.time.LocalDateTime uploadedAt;

    // Getters and setters

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public java.time.LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(java.time.LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public LessonFile() {
    }

    public LessonFile(Lesson lesson, String fileType, String fileName, String fileUrl) {
        this.lesson = lesson;
        this.fileType = fileType;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.uploadedAt = java.time.LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "LessonFile{" +
                "fileId=" + fileId +
                ", lesson=" + lesson +
                ", fileType='" + fileType + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", uploadedAt=" + uploadedAt +
                '}';
    }
}