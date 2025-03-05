package org.example.hlcoursesappserver.controller;

import org.example.hlcoursesappserver.dto.*;
import org.example.hlcoursesappserver.model.*;
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

    // Новые эндпоинты для тестов
    @PostMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests")
    public ResponseEntity<?> createTest(@PathVariable Long courseId,
                                        @PathVariable Long moduleId,
                                        @PathVariable Long lessonId,
                                        @Valid @RequestBody TestRequest testRequest) {
        try {
            Test createdTest = courseService.createTest(lessonId, testRequest);
            return new ResponseEntity<>(createdTest, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Ошибка создания теста: урок с ID " + lessonId + " не существует");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла ошибка при создании теста: " + e.getMessage());
        }
    }

    @PostMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests/{testId}/questions")
    public ResponseEntity<?> createQuestion(@PathVariable Long courseId,
                                            @PathVariable Long moduleId,
                                            @PathVariable Long lessonId,
                                            @PathVariable Long testId,
                                            @Valid @RequestBody QuestionRequest questionRequest) {
        try {
            Question createdQuestion = courseService.createQuestion(testId, questionRequest);
            return new ResponseEntity<>(createdQuestion, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Ошибка создания вопроса: тест с ID " + testId + " не существует");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла ошибка при создании вопроса: " + e.getMessage());
        }
    }

    @PostMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests/{testId}/questions/{questionId}/answers")
    public ResponseEntity<?> createAnswer(@PathVariable Long courseId,
                                          @PathVariable Long moduleId,
                                          @PathVariable Long lessonId,
                                          @PathVariable Long testId,
                                          @PathVariable Long questionId,
                                          @Valid @RequestBody AnswerRequest answerRequest) {
        try {
            Answer createdAnswer = courseService.createAnswer(questionId, answerRequest);
            return new ResponseEntity<>(createdAnswer, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Ошибка создания ответа: вопрос с ID " + questionId + " не существует");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла ошибка при создании ответа: " + e.getMessage());
        }
    }

    // Новый эндпоинт для удаления курса
    @DeleteMapping("/{courseId}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long courseId) {
        try {
            courseService.deleteCourse(courseId);
            return new ResponseEntity<>("Курс с ID " + courseId + " успешно удален", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при удалении курса: " + e.getMessage());
        }
    }

    // Новые эндпоинты для удаления отдельных сущностей
    @DeleteMapping("/{courseId}/modules/{moduleId}")
    public ResponseEntity<?> deleteModule(@PathVariable Long courseId,
                                          @PathVariable Long moduleId) {
        try {
            courseService.deleteModule(moduleId);
            return new ResponseEntity<>("Модуль с ID " + moduleId + " успешно удален", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при удалении модуля: " + e.getMessage());
        }
    }

    @DeleteMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<?> deleteLesson(@PathVariable Long courseId,
                                          @PathVariable Long moduleId,
                                          @PathVariable Long lessonId) {
        try {
            courseService.deleteLesson(lessonId);
            return new ResponseEntity<>("Урок с ID " + lessonId + " успешно удален", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при удалении урока: " + e.getMessage());
        }
    }

    @DeleteMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests/{testId}")
    public ResponseEntity<?> deleteTest(@PathVariable Long courseId,
                                        @PathVariable Long moduleId,
                                        @PathVariable Long lessonId,
                                        @PathVariable Long testId) {
        try {
            courseService.deleteTest(testId);
            return new ResponseEntity<>("Тест с ID " + testId + " успешно удален", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при удалении теста: " + e.getMessage());
        }
    }

    @DeleteMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests/{testId}/questions/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long courseId,
                                            @PathVariable Long moduleId,
                                            @PathVariable Long lessonId,
                                            @PathVariable Long testId,
                                            @PathVariable Long questionId) {
        try {
            courseService.deleteQuestion(questionId);
            return new ResponseEntity<>("Вопрос с ID " + questionId + " успешно удален", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при удалении вопроса: " + e.getMessage());
        }
    }

    @DeleteMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests/{testId}/questions/{questionId}/answers/{answerId}")
    public ResponseEntity<?> deleteAnswer(@PathVariable Long courseId,
                                          @PathVariable Long moduleId,
                                          @PathVariable Long lessonId,
                                          @PathVariable Long testId,
                                          @PathVariable Long questionId,
                                          @PathVariable Long answerId) {
        try {
            courseService.deleteAnswer(answerId);
            return new ResponseEntity<>("Ответ с ID " + answerId + " успешно удален", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при удалении ответа: " + e.getMessage());
        }
    }
}
