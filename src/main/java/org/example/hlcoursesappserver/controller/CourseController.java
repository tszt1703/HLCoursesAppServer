package org.example.hlcoursesappserver.controller;

import org.example.hlcoursesappserver.dto.CourseRequest;
import org.example.hlcoursesappserver.dto.CourseUpdateRequest;
import org.example.hlcoursesappserver.dto.ModuleRequest;
import org.example.hlcoursesappserver.dto.LessonRequest;
import org.example.hlcoursesappserver.model.Course;
import org.example.hlcoursesappserver.model.CourseModule;
import org.example.hlcoursesappserver.model.Lesson;
import org.example.hlcoursesappserver.service.CourseService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/courses")
@Validated
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * Эндпоинт для создания нового курса.
     * Идентификатор специалиста передается с клиентской стороны в теле запроса.
     * Категория создается, если не существует.
     */
    @PostMapping
    public ResponseEntity<?> createCourse(@Valid @RequestBody CourseRequest courseRequest) {
        try {
            Long specialistId = courseRequest.getSpecialistId();
            Course createdCourse = courseService.createCourse(courseRequest, specialistId);
            return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            // Обработка нарушения целостности данных (например, если specialistId не существует)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Ошибка создания курса: неверный specialistId или другая проблема с данными - " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // Обработка пользовательских исключений из сервиса (если они добавлены)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            // Общая обработка ошибок
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла ошибка при создании курса: " + e.getMessage());
        }
    }

    /**
     * Эндпоинт для обновления курса.
     */
    @PutMapping("/{courseId}")
    public ResponseEntity<?> updateCourse(@PathVariable Long courseId,
                                          @Valid @RequestBody CourseUpdateRequest updateRequest) {
        try {
            Optional<Course> updatedCourseOpt = courseService.updateCourse(courseId, updateRequest);
            if (updatedCourseOpt.isPresent()) {
                return new ResponseEntity<>(updatedCourseOpt.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Курс с идентификатором " + courseId + " не найден", HttpStatus.NOT_FOUND);
            }
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Ошибка обновления курса: неверный categoryId или другая проблема с данными - " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла ошибка при обновлении курса: " + e.getMessage());
        }
    }

    /**
     * Эндпоинт для создания модуля в курсе.
     */
    @PostMapping("/{courseId}/modules")
    public ResponseEntity<?> createModule(@PathVariable Long courseId,
                                          @Valid @RequestBody ModuleRequest moduleRequest) {
        try {
            CourseModule createdModule = courseService.createModule(courseId, moduleRequest);
            return new ResponseEntity<>(createdModule, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Ошибка создания модуля: курс с ID " + courseId + " не существует");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла ошибка при создании модуля: " + e.getMessage());
        }
    }

    /**
     * Эндпоинт для создания урока в модуле.
     * Здесь для однозначности URL включаем courseId, хотя для создания урока достаточно moduleId.
     */
    @PostMapping("/{courseId}/modules/{moduleId}/lessons")
    public ResponseEntity<?> createLesson(@PathVariable Long courseId,
                                          @PathVariable Long moduleId,
                                          @Valid @RequestBody LessonRequest lessonRequest) {
        try {
            Lesson createdLesson = courseService.createLesson(moduleId, lessonRequest);
            return new ResponseEntity<>(createdLesson, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Ошибка создания урока: модуль с ID " + moduleId + " не существует");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла ошибка при создании урока: " + e.getMessage());
        }
    }
}
