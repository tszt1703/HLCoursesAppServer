package org.example.hlcoursesappserver.controller;

import org.example.hlcoursesappserver.dto.*;
import org.example.hlcoursesappserver.model.*;
import org.example.hlcoursesappserver.service.CourseApplicationService;
import org.example.hlcoursesappserver.service.CourseService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/courses")
@Validated
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);


    private final CourseService courseService;
    private final CourseApplicationService applicationService;

    @Autowired
    public CourseController(CourseService courseService, CourseApplicationService applicationService) {
        this.courseService = courseService;
        this.applicationService = applicationService;
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
                    .body("Ошибка обновления курса: проблема с данными - " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при обновлении курса: " + e.getMessage());
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

    // Новые эндпоинты для обновления
    @PutMapping("/{courseId}/modules/{moduleId}")
    public ResponseEntity<?> updateModule(@PathVariable Long courseId,
                                          @PathVariable Long moduleId,
                                          @Valid @RequestBody ModuleUpdateRequest updateRequest) {
        try {
            Optional<CourseModule> updatedModuleOpt = courseService.updateModule(moduleId, updateRequest);
            if (updatedModuleOpt.isPresent()) {
                return new ResponseEntity<>(updatedModuleOpt.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Модуль с ID " + moduleId + " не найден", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при обновлении модуля: " + e.getMessage());
        }
    }

    @PutMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<?> updateLesson(@PathVariable Long courseId,
                                          @PathVariable Long moduleId,
                                          @PathVariable Long lessonId,
                                          @Valid @RequestBody LessonUpdateRequest updateRequest) {
        try {
            Optional<Lesson> updatedLessonOpt = courseService.updateLesson(lessonId, updateRequest);
            if (updatedLessonOpt.isPresent()) {
                return new ResponseEntity<>(updatedLessonOpt.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Урок с ID " + lessonId + " не найден", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при обновлении урока: " + e.getMessage());
        }
    }

    @PutMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests/{testId}")
    public ResponseEntity<?> updateTest(@PathVariable Long courseId,
                                        @PathVariable Long moduleId,
                                        @PathVariable Long lessonId,
                                        @PathVariable Long testId,
                                        @Valid @RequestBody TestUpdateRequest updateRequest) {
        try {
            Optional<Test> updatedTestOpt = courseService.updateTest(testId, updateRequest);
            if (updatedTestOpt.isPresent()) {
                return new ResponseEntity<>(updatedTestOpt.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Тест с ID " + testId + " не найден", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при обновлении теста: " + e.getMessage());
        }
    }

    @PutMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests/{testId}/questions/{questionId}")
    public ResponseEntity<?> updateQuestion(@PathVariable Long courseId,
                                            @PathVariable Long moduleId,
                                            @PathVariable Long lessonId,
                                            @PathVariable Long testId,
                                            @PathVariable Long questionId,
                                            @Valid @RequestBody QuestionUpdateRequest updateRequest) {
        try {
            Optional<Question> updatedQuestionOpt = courseService.updateQuestion(questionId, updateRequest);
            if (updatedQuestionOpt.isPresent()) {
                return new ResponseEntity<>(updatedQuestionOpt.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Вопрос с ID " + questionId + " не найден", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при обновлении вопроса: " + e.getMessage());
        }
    }

    @PutMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests/{testId}/questions/{questionId}/answers/{answerId}")
    public ResponseEntity<?> updateAnswer(@PathVariable Long courseId,
                                          @PathVariable Long moduleId,
                                          @PathVariable Long lessonId,
                                          @PathVariable Long testId,
                                          @PathVariable Long questionId,
                                          @PathVariable Long answerId,
                                          @Valid @RequestBody AnswerUpdateRequest updateRequest) {
        try {
            Optional<Answer> updatedAnswerOpt = courseService.updateAnswer(answerId, updateRequest);
            if (updatedAnswerOpt.isPresent()) {
                return new ResponseEntity<>(updatedAnswerOpt.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Ответ с ID " + answerId + " не найден", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при обновлении ответа: " + e.getMessage());
        }
    }

    // Новые эндпоинты для поиска и фильтрации
    @GetMapping("/search")
    public ResponseEntity<?> searchCoursesByTitle(@RequestParam("title") String title) {
        try {
            List<Course> courses = courseService.searchCoursesByTitle(title);
            return new ResponseEntity<>(courses, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при поиске курсов: " + e.getMessage());
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterCourses(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "ageGroup", required = false) String ageGroup,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "difficultyLevel", required = false) String difficultyLevel,
            @RequestParam(value = "durationDays", required = false) Integer durationDays) {
        try {
            List<Course> courses = courseService.filterCourses(title, ageGroup, categoryId, difficultyLevel, durationDays);
            return new ResponseEntity<>(courses, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при фильтрации курсов: " + e.getMessage());
        }
    }

    // Новый эндпоинт для получения полной информации о курсе
    @GetMapping("/{courseId}")
    public ResponseEntity<?> getCourseWithDetails(@PathVariable Long courseId) {
        try {
            Optional<Course> courseOpt = courseService.getCourseWithDetails(courseId);
            if (courseOpt.isPresent()) {
                return new ResponseEntity<>(courseOpt.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Курс с ID " + courseId + " не найден", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при получении курса: " + e.getMessage());
        }
    }

    @PostMapping("/{courseId}/apply")
    public ResponseEntity<?> applyForCourse(
            @PathVariable Long courseId,
            @RequestHeader("userId") Long listenerId) {
        logger.info("Получена заявка на курс ID: {} от слушателя ID: {}", courseId, listenerId);

        try {
            CourseApplication application = applicationService.applyForCourse(listenerId, courseId);
            return ResponseEntity.ok(Map.of("message", "Заявка успешно подана", "applicationId", application.getId()));
        } catch (RuntimeException e) {
            logger.error("Ошибка при подаче заявки: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/applications/{applicationId}/status")
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestHeader("email") String specialistEmail,
            @RequestBody Map<String, String> statusBody) {
        logger.info("Запрос на обновление статуса заявки ID: {} от специалиста: {}", applicationId, specialistEmail);

        try {
            CourseApplication.ApplicationStatus newStatus = CourseApplication.ApplicationStatus.valueOf(statusBody.get("status"));
            CourseApplication updatedApplication = applicationService.updateApplicationStatus(applicationId, specialistEmail, newStatus);
            return ResponseEntity.ok(Map.of("message", "Статус заявки обновлён", "status", updatedApplication.getStatus().toString()));
        } catch (IllegalArgumentException e) {
            logger.error("Некорректный статус: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Некорректный статус"));
        } catch (RuntimeException e) {
            logger.error("Ошибка при обновлении статуса: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{courseId}/applications/pending")
    public ResponseEntity<List<CourseApplication>> getPendingApplications(
            @PathVariable Long courseId,
            @RequestHeader("email") String specialistEmail) {
        logger.info("Запрос списка ожидающих заявок для курса ID: {} от специалиста: {}", courseId, specialistEmail);

        List<CourseApplication> applications = applicationService.getPendingApplicationsForCourse(courseId);
        return ResponseEntity.ok(applications);
    }

    /**
     * Эндпоинт для отметки завершения урока слушателем
     */
    @PostMapping("/{courseId}/lessons/{lessonId}/complete")
    public ResponseEntity<?> completeLesson(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @RequestHeader("userId") Long listenerId) {
        logger.info("Запрос на завершение урока ID: {} для курса ID: {} от слушателя ID: {}",
                lessonId, courseId, listenerId);

        try {
            courseService.completeLesson(listenerId, courseId, lessonId);
            return ResponseEntity.ok(Map.of("message", "Урок успешно завершён"));
        } catch (IllegalStateException e) {
            logger.error("Ошибка: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Ошибка при завершении урока: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при завершении урока: " + e.getMessage()));
        }
    }

    /**
     * Эндпоинт для отметки прохождения теста слушателем
     */
    @PostMapping("/{courseId}/tests/{testId}/pass")
    public ResponseEntity<?> passTest(
            @PathVariable Long courseId,
            @PathVariable Long testId,
            @RequestHeader("userId") Long listenerId) {
        logger.info("Запрос на прохождение теста ID: {} для курса ID: {} от слушателя ID: {}",
                testId, courseId, listenerId);

        try {
            courseService.passTest(listenerId, courseId, testId);
            return ResponseEntity.ok(Map.of("message", "Тест успешно пройден"));
        } catch (IllegalStateException e) {
            logger.error("Ошибка: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Ошибка при прохождении теста: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при прохождении теста: " + e.getMessage()));
        }
    }

    /**
     * Эндпоинт для получения статистики прогресса слушателя по курсу
     */
    @GetMapping("/{courseId}/progress")
    public ResponseEntity<?> getProgressStat(
            @PathVariable Long courseId,
            @RequestHeader("userId") Long listenerId) {
        logger.info("Запрос статистики прогресса для курса ID: {} от слушателя ID: {}",
                courseId, listenerId);

        try {
            Optional<ProgressStat> progressOpt = courseService.getProgressStat(listenerId, courseId);
            if (progressOpt.isPresent()) {
                return new ResponseEntity<>(progressOpt.get(), HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Статистика для данного курса и слушателя не найдена"));
            }
        } catch (Exception e) {
            logger.error("Ошибка при получении статистики: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при получении статистики: " + e.getMessage()));
        }
    }

    /**
     * Эндпоинт для получения списка всех курсов слушателя с их прогрессом
     */
    @GetMapping("/my-progress")
    public ResponseEntity<?> getAllProgressForListener(
            @RequestHeader("userId") Long listenerId) {
        logger.info("Запрос списка прогресса всех курсов для слушателя ID: {}", listenerId);

        try {
            List<ProgressStat> progressList = courseService.getAllProgressForListener(listenerId);
            return new ResponseEntity<>(progressList, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Ошибка при получении списка прогресса: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при получении списка прогресса: " + e.getMessage()));
        }
    }

    // Новый эндпоинт для получения списка всех категорий
    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories() {
        try {
            List<CourseCategory> categories = courseService.getAllCategories();
            return new ResponseEntity<>(categories, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при получении списка категорий: " + e.getMessage());
        }
    }

    // Новый эндпоинт для получения названия категории по categoryId
    @GetMapping("/categories/{categoryId}/name")
    public ResponseEntity<?> getCategoryName(@PathVariable Long categoryId) {
        try {
            String categoryName = courseService.getCategoryNameById(categoryId);
            return new ResponseEntity<>(categoryName, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при получении названия категории: " + e.getMessage());
        }
    }
}
