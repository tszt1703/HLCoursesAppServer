package org.example.hlcoursesappserver.controller;

import org.example.hlcoursesappserver.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * Загружает фото профиля пользователя (требует аутентификации).
     *
     * @param file   файл фото профиля
     * @param userId идентификатор пользователя
     * @param role   роль пользователя ("Specialist" или "Listener")
     * @return JSON с URL загруженного файла
     */
    @PostMapping("/upload-profile-photo")
    public ResponseEntity<?> uploadProfilePhoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId,
            @RequestParam("role") String role) {
        try {
            String fileUrl = fileService.uploadProfilePhoto(file, userId, role);
            return ResponseEntity.ok().body(Map.of("fileUrl", fileUrl));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка при загрузке фото профиля: " + e.getMessage()));
        }
    }

    /**
     * Получает URL фото профиля пользователя (публичный).
     *
     * @param userId идентификатор пользователя
     * @param role   роль пользователя ("Specialist" или "Listener")
     * @return JSON с URL фото профиля
     */
    @GetMapping("/profile-photo")
    public ResponseEntity<?> getProfilePhotoUrl(
            @RequestParam("userId") Long userId,
            @RequestParam("role") String role) {
        try {
            String fileUrl = fileService.getProfilePhotoUrl(userId, role);
            return ResponseEntity.ok().body(Map.of("fileUrl", fileUrl));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", "Фото профиля не найдено: " + e.getMessage()));
        }
    }

    /**
     * Загружает обложку курса (требует аутентификации).
     *
     * @param file     файл обложки курса
     * @param courseId идентификатор курса
     * @return JSON с URL загруженного файла
     */
    @PostMapping("/upload-course-cover")
    public ResponseEntity<?> uploadCourseCover(
            @RequestParam("file") MultipartFile file,
            @RequestParam("courseId") Long courseId) {
        try {
            String fileUrl = fileService.uploadCourseCover(file, courseId);
            return ResponseEntity.ok().body(Map.of("fileUrl", fileUrl));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка при загрузке обложки курса: " + e.getMessage()));
        }
    }

    /**
     * Загружает видео для курса (требует аутентификации).
     *
     * @param file     файл видео
     * @param courseId идентификатор курса
     * @return JSON с URL загруженного файла
     */
    @PostMapping("/upload-course-video")
    public ResponseEntity<?> uploadCourseVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("courseId") Long courseId) {
        try {
            String fileUrl = fileService.uploadCourseVideo(file, courseId);
            return ResponseEntity.ok().body(Map.of("fileUrl", fileUrl));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка при загрузке видео курса: " + e.getMessage()));
        }
    }

    /**
     * Получает URL обложки курса (публичный).
     *
     * @param courseId идентификатор курса
     * @return JSON с URL обложки курса
     */
    @GetMapping("/course-cover")
    public ResponseEntity<?> getCourseCover(
            @RequestParam("courseId") Long courseId) {
        try {
            String coverUrl = fileService.getCourseCoverUrl(courseId);
            return ResponseEntity.ok().body(Map.of("coverUrl", coverUrl));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }
}