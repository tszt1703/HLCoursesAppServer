package org.example.hlcoursesappserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.hlcoursesappserver.model.LessonFile;
import org.example.hlcoursesappserver.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
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
     * Эндпоинт для загрузки фото профиля.
     *
     * @param file   файл фото профиля
     * @param userId идентификатор пользователя
     * @param role   роль пользователя
     * @return ответ с сообщением об успешной загрузке
     */
    @PostMapping("/upload-profile-photo")
    public ResponseEntity<?> uploadProfilePhoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId,
            @RequestParam("role") String role) {
        try {
            String fileUrl = fileService.uploadProfilePhoto(file, userId, role);
            return ResponseEntity.ok().body("Фото профиля успешно загружено: " + fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Ошибка при загрузке фото профиля: " + e.getMessage());
        }
    }

    /**
     * Эндпоинт для получения URL фото профиля.
     *
     * @param userId идентификатор пользователя
     * @param role   роль пользователя
     * @return URL фото профиля
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
     * Эндпоинт для загрузки обложки курса.
     *
     * @param file     файл обложки курса
     * @param courseId идентификатор курса
     * @return ответ с сообщением об успешной загрузке
     */
    @Operation(summary = "Загрузить фото профиля", description = "Загружает фото профиля для пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Фото профиля успешно загружено"),
            @ApiResponse(responseCode = "400", description = "Ошибка при загрузке фото профиля", content = @Content),
            @ApiResponse(responseCode = "500", description = "Ошибка при загрузке фото профиля", content = @Content)
    })
    @PostMapping("/upload-course-cover")
    public ResponseEntity<?> uploadCourseCover(
            @RequestParam("file") MultipartFile file,
            @RequestParam("courseId") Long courseId) {
        try {
            String fileUrl = fileService.uploadCourseCover(file, courseId);
            return ResponseEntity.ok().body("Обложка курса успешно загружена: " + fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Ошибка при загрузке обложки курса: " + e.getMessage());
        }
    }

    /**
     * Эндпоинт для получения обложки курса.
     *
     * @param courseId идентификатор курса
     * @return URL обложки курса
     */
    @Operation(summary = "Получить обложку курса", description = "Получает URL обложки курса")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Обложка курса успешно получена"),
            @ApiResponse(responseCode = "400", description = "Ошибка при получении обложки курса", content = @Content),
            @ApiResponse(responseCode = "404", description = "Обложка курса не найдена", content = @Content)
    })
    @GetMapping("/course-cover")
    public ResponseEntity<?> getCourseCover(@RequestParam("courseId") Long courseId) {
        try {
            String coverUrl = fileService.getCourseCoverUrl(courseId);
            return ResponseEntity.ok().body(Map.of("coverUrl", coverUrl));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Эндпоинт для удаления фото профиля.
     *
     * @param userId идентификатор пользователя
     * @param role   роль пользователя
     * @return ответ с сообщением об успешном удалении
     */
    @Operation(summary = "Удалить фото профиля", description = "Удаляет фото профиля для пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Фото профиля успешно удалено"),
            @ApiResponse(responseCode = "400", description = "Ошибка при удалении фото профиля", content = @Content),
            @ApiResponse(responseCode = "404", description = "Фото профиля не найдено", content = @Content)
    })
    @DeleteMapping("/delete-profile-photo")
    public ResponseEntity<?> deleteProfilePhoto(
            @RequestParam("userId") Long userId,
            @RequestParam("role") String role) {
        try {
            fileService.deleteProfilePhoto(userId, role);
            return ResponseEntity.ok().body("Фото профиля успешно удалено.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Ошибка при удалении фото профиля: " + e.getMessage());
        }
    }

    /**
     * Эндпоинт для удаления обложки курса.
     *
     * @param courseId идентификатор курса
     * @return ответ с сообщением об успешном удалении
     */
    @Operation(summary = "Удалить обложку курса", description = "Удаляет обложку курса")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Обложка курса успешно удалена"),
            @ApiResponse(responseCode = "400", description = "Ошибка при удалении обложки курса", content = @Content),
            @ApiResponse(responseCode = "404", description = "Обложка курса не найдена", content = @Content)
    })
    @DeleteMapping("/delete-course-cover")
    public ResponseEntity<?> deleteCourseCover(@RequestParam("courseId") Long courseId) {
        try {
            fileService.deleteCourseCover(courseId);
            return ResponseEntity.ok().body("Обложка курса успешно удалена.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Ошибка при удалении обложки курса: " + e.getMessage());
        }
    }

    /**
     * Эндпоинт для загрузки файла урока.
     *
     * @param file     файл урока
     * @param lessonId идентификатор урока
     * @param fileType тип файла (например, 'photo', 'video', 'document')
     * @return ответ с сообщением об успешной загрузке
     */
    @Operation(summary = "Загрузить файл урока", description = "Загружает файл урока")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Файл урока успешно загружен"),
            @ApiResponse(responseCode = "400", description = "Ошибка при загрузке файла урока", content = @Content),
            @ApiResponse(responseCode = "500", description = "Ошибка при загрузке файла урока", content = @Content)
    })
    @PostMapping("/upload-lesson-file")
    public ResponseEntity<?> uploadLessonFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("lessonId") Long lessonId,
            @RequestParam("fileType") String fileType) {
        try {
            LessonFile lessonFile = fileService.uploadLessonFile(file, lessonId, fileType);
            return ResponseEntity.ok().body("Файл урока успешно загружен: " + lessonFile.getFileUrl());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Ошибка при загрузке файла урока: " + e.getMessage());
        }
    }

    /**
     * Эндпоинт для получения файлов урока.
     *
     * @param lessonId идентификатор урока
     * @return список файлов урока
     */
    @Operation(summary = "Получить файлы урока", description = "Получает список файлов урока")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Файлы урока успешно получены"),
            @ApiResponse(responseCode = "400", description = "Ошибка при получении файлов урока", content = @Content),
            @ApiResponse(responseCode = "404", description = "Файлы урока не найдены", content = @Content)
    })
    @GetMapping("/lesson-files")
    public ResponseEntity<?> getLessonFiles(@RequestParam("lessonId") Long lessonId) {
        try {
            List<LessonFile> files = fileService.getLessonFiles(lessonId);
            return ResponseEntity.ok().body(files);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Ошибка при получении файлов урока: " + e.getMessage());
        }
    }

    /**
     * Эндпоинт для удаления файлов урока.
     *
     * @param lessonId идентификатор урока
     * @return ответ с сообщением об успешном удалении
     */
    @Operation(summary = "Удалить файлы урока", description = "Удаляет файлы урока")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Файлы урока успешно удалены"),
            @ApiResponse(responseCode = "400", description = "Ошибка при удалении файлов урока", content = @Content),
            @ApiResponse(responseCode = "404", description = "Файлы урока не найдены", content = @Content)
    })
    @DeleteMapping("/delete-lesson-files")
    public ResponseEntity<?> deleteLessonFiles(@RequestParam("lessonId") Long lessonId) {
        try {
            fileService.deleteLessonFiles(lessonId);
            return ResponseEntity.ok().body("Файлы урока успешно удалены.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Ошибка при удалении файлов урока: " + e.getMessage());
        }
    }
}
