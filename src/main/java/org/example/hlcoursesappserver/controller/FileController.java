package org.example.hlcoursesappserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.hlcoursesappserver.model.LessonFile;
import org.example.hlcoursesappserver.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@Validated
public class FileController {

    private final FileService fileService;

    @Value("${file.storage.path:/uploads}")
    private String storagePath;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @Operation(summary = "Загрузить фото профиля", description = "Загружает фото профиля для пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Фото профиля успешно загружено",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Неверные входные данные",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера при загрузке файла",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping(value = "/upload-profile-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProfilePhoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") @NotNull Long userId,
            @RequestParam("role") @NotBlank String role) {
        try {
            String fileUrl = fileService.uploadProfilePhoto(file, userId, role);
            return ResponseEntity.ok().body(Map.of("message", "Фото профиля успешно загружено", "fileUrl", fileUrl));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка при загрузке фото профиля: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Получить URL фото профиля", description = "Возвращает URL фото профиля пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "URL фото профиля успешно получен",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Фото профиля не найдено",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/profile-photo")
    public ResponseEntity<?> getProfilePhotoUrl(
            @RequestParam("userId") @NotNull Long userId,
            @RequestParam("role") @NotBlank String role) {
        try {
            String fileUrl = fileService.getProfilePhotoUrl(userId, role);
            return ResponseEntity.ok().body(Map.of("fileUrl", fileUrl));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", "Фото профиля не найдено: " + e.getMessage()));
        }
    }

    @Operation(summary = "Загрузить обложку курса", description = "Загружает обложку для курса")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Обложка курса успешно загружена",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Неверные входные данные",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера при загрузке файла",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping(value = "/upload-course-cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadCourseCover(
            @RequestParam("file") MultipartFile file,
            @RequestParam("courseId") @NotNull Long courseId) {
        try {
            String fileUrl = fileService.uploadCourseCover(file, courseId);
            return ResponseEntity.ok().body(Map.of("message", "Обложка курса успешно загружена", "fileUrl", fileUrl));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка при загрузке обложки курса: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @Operation(summary = "Получить обложку курса", description = "Возвращает URL обложки курса")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Обложка курса успешно получена",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Обложка курса не найдена",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/course-cover")
    public ResponseEntity<?> getCourseCover(@RequestParam("courseId") @NotNull Long courseId) {
        try {
            String coverUrl = fileService.getCourseCoverUrl(courseId);
            return ResponseEntity.ok().body(Map.of("coverUrl", coverUrl));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Удалить фото профиля", description = "Удаляет фото профиля пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Фото профиля успешно удалено",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Фото профиля не найдено",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @DeleteMapping("/delete-profile-photo")
    public ResponseEntity<?> deleteProfilePhoto(
            @RequestParam("userId") @NotNull Long userId,
            @RequestParam("role") @NotBlank String role) {
        try {
            fileService.deleteProfilePhoto(userId, role);
            return ResponseEntity.ok().body(Map.of("message", "Фото профиля успешно удалено"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Удалить обложку курса", description = "Удаляет обложку курса")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Обложка курса успешно удалена",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Обложка курса не найдена",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @DeleteMapping("/delete-course-cover")
    public ResponseEntity<?> deleteCourseCover(@RequestParam("courseId") @NotNull Long courseId) {
        try {
            fileService.deleteCourseCover(courseId);
            return ResponseEntity.ok().body(Map.of("message", "Обложка курса успешно удалена"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Загрузить файл урока", description = "Загружает файл урока")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Файл урока успешно загружен",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Неверные входные данные",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера при загрузке файла",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping(value = "/upload-lesson-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadLessonFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("lessonId") @NotNull Long lessonId,
            @RequestParam("fileType") @NotBlank String fileType) {
        try {
            LessonFile lessonFile = fileService.uploadLessonFile(file, lessonId, fileType);
            return ResponseEntity.ok().body(Map.of("message", "Файл урока успешно загружен", "fileUrl", lessonFile.getFileUrl()));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка при загрузке файла урока: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Получить файлы урока", description = "Возвращает список файлов урока")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Файлы урока успешно получены",
                    content = @Content(schema = @Schema(implementation = LessonFile.class))),
            @ApiResponse(responseCode = "404", description = "Файлы урока не найдены",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/lesson-files")
    public ResponseEntity<?> getLessonFiles(@RequestParam("lessonId") @NotNull Long lessonId) {
        try {
            List<LessonFile> files = fileService.getLessonFiles(lessonId);
            return ResponseEntity.ok().body(files);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Удалить файлы урока", description = "Удаляет файлы урока")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Файлы урока успешно удалены",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Файлы урока не найдены",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @DeleteMapping("/delete-lesson-files")
    public ResponseEntity<?> deleteLessonFiles(@RequestParam("lessonId") @NotNull Long lessonId) {
        try {
            fileService.deleteLessonFiles(lessonId);
            return ResponseEntity.ok().body(Map.of("message", "Файлы урока успешно удалены"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Получить файл", description = "Возвращает файл по его имени")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Файл успешно получен"),
            @ApiResponse(responseCode = "404", description = "Файл не найден",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/{filename:.+}")
    public ResponseEntity<?> getFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(storagePath, filename);
            if (!Files.exists(filePath)) {
                return ResponseEntity.status(404).body(Map.of("error", "Файл не найден: " + filename));
            }
            Resource resource = new UrlResource(filePath.toUri());
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream"; // Резервный вариант
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка при получении файла: " + e.getMessage()));
        }
    }
}