package org.example.hlcoursesappserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.hlcoursesappserver.dto.*;
import org.example.hlcoursesappserver.model.*;
import org.example.hlcoursesappserver.service.CourseApplicationService;
import org.example.hlcoursesappserver.service.CourseService;
import org.slf4j.Logger;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/courses")
@Validated
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    private final CourseService courseService;
    private final CourseApplicationService applicationService;

    /**
     * Конструктор контроллера курсов.
     *
     * @param courseService      сервис для работы с курсами
     * @param applicationService сервис для работы с заявками на курсы
     */
    @Autowired
    public CourseController(CourseService courseService, CourseApplicationService applicationService) {
        this.courseService = courseService;
        this.applicationService = applicationService;
    }

    /**
     * Получает список всех курсов.
     *
     * @return список курсов
     */
    @Operation(summary = "Создание нового курса", description = "Создает новый курс.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Курс успешно создан"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Ошибка в данных запроса"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping
    public ResponseEntity<?> createCourse(@Valid @RequestBody CourseRequest courseRequest) {
        try {
            Long specialistId = courseRequest.getSpecialistId();
            Course createdCourse = courseService.createCourse(courseRequest, specialistId);
            CourseDTO courseDTO = new CourseDTO(createdCourse);
            return new ResponseEntity<>(courseDTO, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Ошибка создания курса: неверный specialistId или другая проблема с данными - " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Произошла ошибка при создании курса: " + e.getMessage()));
        }
    }

    /**
     * Получает список всех курсов.
     *
     * @return список курсов
     */
    @Operation(summary = "Обновление курса", description = "Обновляет существующий курс по его ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Курс успешно обновлен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Курс не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Ошибка в данных запроса"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PutMapping("/{courseId}")
    public ResponseEntity<?> updateCourse(@PathVariable Long courseId,
                                          @Valid @RequestBody CourseUpdateRequest updateRequest) {
        try {
            Optional<Course> updatedCourseOpt = courseService.updateCourse(courseId, updateRequest);
            if (updatedCourseOpt.isPresent()) {
                CourseDTO courseDTO = new CourseDTO(updatedCourseOpt.get());
                return new ResponseEntity<>(courseDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("error", "Курс с идентификатором " + courseId + " не найден"), HttpStatus.NOT_FOUND);
            }
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Ошибка обновления курса: проблема с данными - " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при обновлении курса: " + e.getMessage()));
        }
    }

    /**
     * Получает курс по его ID.
     *
     * @param courseId идентификатор курса
     * @return курс с указанным ID
     */
    @Operation(summary = "Создание модуля", description = "Создает новый модуль для курса.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Модуль успешно создан"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Ошибка в данных запроса"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping("/{courseId}/modules")
    public ResponseEntity<?> createModule(@PathVariable Long courseId,
                                          @Valid @RequestBody ModuleRequest moduleRequest) {
        try {
            CourseModule createdModule = courseService.createModule(courseId, moduleRequest);
            CourseModuleDTO moduleDTO = new CourseModuleDTO(createdModule);
            return new ResponseEntity<>(moduleDTO, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Ошибка создания модуля: курс с ID " + courseId + " не существует"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Произошла ошибка при создании модуля: " + e.getMessage()));
        }
    }


    @Operation(summary = "Создание урока", description = "Создает новый урок для модуля.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Урок успешно создан"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Ошибка в данных запроса"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping("/{courseId}/modules/{moduleId}/lessons")
    public ResponseEntity<?> createLesson(@PathVariable Long courseId,
                                          @PathVariable Long moduleId,
                                          @Valid @RequestBody LessonRequest lessonRequest) {
        try {
            Lesson createdLesson = courseService.createLesson(moduleId, lessonRequest);
            LessonDTO lessonDTO = new LessonDTO(createdLesson);
            return new ResponseEntity<>(lessonDTO, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Ошибка создания урока: модуль с ID " + moduleId + " не существует"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Произошла ошибка при создании урока: " + e.getMessage()));
        }
    }

    @Operation(summary = "Создание теста", description = "Создает новый тест для урока.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Тест успешно создан"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Ошибка в данных запроса"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests")
    public ResponseEntity<?> createTest(@PathVariable Long courseId,
                                        @PathVariable Long moduleId,
                                        @PathVariable Long lessonId,
                                        @Valid @RequestBody TestRequest testRequest) {
        try {
            Test createdTest = courseService.createTest(lessonId, testRequest);
            TestDTO testDTO = new TestDTO(createdTest);
            return new ResponseEntity<>(testDTO, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Ошибка создания теста: урок с ID " + lessonId + " не существует"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Произошла ошибка при создании теста: " + e.getMessage()));
        }
    }

    @Operation(summary = "Создание вопроса", description = "Создает новый вопрос для теста.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Вопрос успешно создан"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Ошибка в данных запроса"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests/{testId}/questions")
    public ResponseEntity<?> createQuestion(@PathVariable Long courseId,
                                            @PathVariable Long moduleId,
                                            @PathVariable Long lessonId,
                                            @PathVariable Long testId,
                                            @Valid @RequestBody QuestionRequest questionRequest) {
        try {
            Question createdQuestion = courseService.createQuestion(testId, questionRequest);
            QuestionDTO questionDTO = new QuestionDTO(createdQuestion);
            return new ResponseEntity<>(questionDTO, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Ошибка создания вопроса: тест с ID " + testId + " не существует"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Произошла ошибка при создании вопроса: " + e.getMessage()));
        }
    }

    @Operation(summary = "Создание ответа", description = "Создает новый ответ для вопроса.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Ответ успешно создан"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Ошибка в данных запроса"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests/{testId}/questions/{questionId}/answers")
    public ResponseEntity<?> createAnswer(@PathVariable Long courseId,
                                          @PathVariable Long moduleId,
                                          @PathVariable Long lessonId,
                                          @PathVariable Long testId,
                                          @PathVariable Long questionId,
                                          @Valid @RequestBody AnswerRequest answerRequest) {
        try {
            Answer createdAnswer = courseService.createAnswer(questionId, answerRequest);
            AnswerDTO answerDTO = new AnswerDTO(createdAnswer);
            return new ResponseEntity<>(answerDTO, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Ошибка создания ответа: вопрос с ID " + questionId + " не существует"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Произошла ошибка при создании ответа: " + e.getMessage()));
        }
    }

    @Operation(summary = "Удаление курса", description = "Удаляет курс по его ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Курс успешно удален"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Курс не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @DeleteMapping("/{courseId}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long courseId) {
        try {
            courseService.deleteCourse(courseId);
            return new ResponseEntity<>(Map.of("message", "Курс с ID " + courseId + " успешно удален"), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при удалении курса: " + e.getMessage()));
        }
    }

    @Operation(summary = "Удаление модуля", description = "Удаляет модуль по его ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Модуль успешно удален"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Модуль не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @DeleteMapping("/{courseId}/modules/{moduleId}")
    public ResponseEntity<?> deleteModule(@PathVariable Long courseId,
                                          @PathVariable Long moduleId) {
        try {
            courseService.deleteModule(moduleId);
            return new ResponseEntity<>(Map.of("message", "Модуль с ID " + moduleId + " успешно удален"), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при удалении модуля: " + e.getMessage()));
        }
    }

    @Operation(summary = "Удаление урока", description = "Удаляет урок по его ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Урок успешно удален"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Урок не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @DeleteMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<?> deleteLesson(@PathVariable Long courseId,
                                          @PathVariable Long moduleId,
                                          @PathVariable Long lessonId) {
        try {
            courseService.deleteLesson(lessonId);
            return new ResponseEntity<>(Map.of("message", "Урок с ID " + lessonId + " успешно удален"), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при удалении урока: " + e.getMessage()));
        }
    }

    @Operation(summary = "Удаление теста", description = "Удаляет тест по его ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Тест успешно удален"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Тест не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @DeleteMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests/{testId}")
    public ResponseEntity<?> deleteTest(@PathVariable Long courseId,
                                        @PathVariable Long moduleId,
                                        @PathVariable Long lessonId,
                                        @PathVariable Long testId) {
        try {
            courseService.deleteTest(testId);
            return new ResponseEntity<>(Map.of("message", "Тест с ID " + testId + " успешно удален"), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при удалении теста: " + e.getMessage()));
        }
    }

    @Operation(summary = "Удаление вопроса", description = "Удаляет вопрос по его ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Вопрос успешно удален"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Вопрос не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @DeleteMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests/{testId}/questions/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long courseId,
                                            @PathVariable Long moduleId,
                                            @PathVariable Long lessonId,
                                            @PathVariable Long testId,
                                            @PathVariable Long questionId) {
        try {
            courseService.deleteQuestion(questionId);
            return new ResponseEntity<>(Map.of("message", "Вопрос с ID " + questionId + " успешно удален"), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при удалении вопроса: " + e.getMessage()));
        }
    }

    @Operation(summary = "Удаление ответа", description = "Удаляет ответ по его ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ответ успешно удален"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ответ не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @DeleteMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests/{testId}/questions/{questionId}/answers/{answerId}")
    public ResponseEntity<?> deleteAnswer(@PathVariable Long courseId,
                                          @PathVariable Long moduleId,
                                          @PathVariable Long lessonId,
                                          @PathVariable Long testId,
                                          @PathVariable Long questionId,
                                          @PathVariable Long answerId) {
        try {
            courseService.deleteAnswer(answerId);
            return new ResponseEntity<>(Map.of("message", "Ответ с ID " + answerId + " успешно удален"), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при удалении ответа: " + e.getMessage()));
        }
    }

    @Operation(summary = "Обновление модуля", description = "Обновляет существующий модуль по его ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Модуль успешно обновлен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Модуль не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Ошибка в данных запроса"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PutMapping("/{courseId}/modules/{moduleId}")
    public ResponseEntity<?> updateModule(@PathVariable Long courseId,
                                          @PathVariable Long moduleId,
                                          @Valid @RequestBody ModuleUpdateRequest updateRequest) {
        try {
            Optional<CourseModule> updatedModuleOpt = courseService.updateModule(moduleId, updateRequest);
            if (updatedModuleOpt.isPresent()) {
                CourseModuleDTO moduleDTO = new CourseModuleDTO(updatedModuleOpt.get());
                return new ResponseEntity<>(moduleDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("error", "Модуль с ID " + moduleId + " не найден"), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при обновлении модуля: " + e.getMessage()));
        }
    }

    @Operation(summary = "Обновление урока", description = "Обновляет существующий урок по его ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Урок успешно обновлен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Урок не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Ошибка в данных запроса"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PutMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<?> updateLesson(@PathVariable Long courseId,
                                          @PathVariable Long moduleId,
                                          @PathVariable Long lessonId,
                                          @Valid @RequestBody LessonUpdateRequest updateRequest) {
        try {
            Optional<Lesson> updatedLessonOpt = courseService.updateLesson(lessonId, updateRequest);
            if (updatedLessonOpt.isPresent()) {
                LessonDTO lessonDTO = new LessonDTO(updatedLessonOpt.get());
                return new ResponseEntity<>(lessonDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("error", "Урок с ID " + lessonId + " не найден"), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при обновлении урока: " + e.getMessage()));
        }
    }

    @Operation(summary = "Обновление теста", description = "Обновляет существующий тест по его ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Тест успешно обновлен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Тест не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Ошибка в данных запроса"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PutMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests/{testId}")
    public ResponseEntity<?> updateTest(@PathVariable Long courseId,
                                        @PathVariable Long moduleId,
                                        @PathVariable Long lessonId,
                                        @PathVariable Long testId,
                                        @Valid @RequestBody TestUpdateRequest updateRequest) {
        try {
            Optional<Test> updatedTestOpt = courseService.updateTest(testId, updateRequest);
            if (updatedTestOpt.isPresent()) {
                TestDTO testDTO = new TestDTO(updatedTestOpt.get());
                return new ResponseEntity<>(testDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("error", "Тест с ID " + testId + " не найден"), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при обновлении теста: " + e.getMessage()));
        }
    }

    @Operation(summary = "Обновление вопроса", description = "Обновляет существующий вопрос по его ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Вопрос успешно обновлен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Вопрос не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Ошибка в данных запроса"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
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
                QuestionDTO questionDTO = new QuestionDTO(updatedQuestionOpt.get());
                return new ResponseEntity<>(questionDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("error", "Вопрос с ID " + questionId + " не найден"), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при обновлении вопроса: " + e.getMessage()));
        }
    }

    @Operation(summary = "Обновление ответа", description = "Обновляет существующий ответ по его ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ответ успешно обновлен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ответ не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Ошибка в данных запроса"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
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
                AnswerDTO answerDTO = new AnswerDTO(updatedAnswerOpt.get());
                return new ResponseEntity<>(answerDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("error", "Ответ с ID " + answerId + " не найден"), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при обновлении ответа: " + e.getMessage()));
        }
    }

    @Operation(summary = "Получение всех курсов", description = "Возвращает список всех курсов.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Список курсов успешно получен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/search")
    public ResponseEntity<?> searchCoursesByTitle(@RequestParam("title") String title) {
        try {
            List<Course> courses = courseService.searchCoursesByTitle(title);
            List<CourseDTO> courseDTOs = courses.stream()
                    .map(CourseDTO::new)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(courseDTOs, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при поиске курсов: " + e.getMessage()));
        }
    }

    @Operation(summary = "Фильтрация курсов", description = "Возвращает список курсов по заданным критериям.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Список курсов успешно получен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/filter")
    public ResponseEntity<?> filterCourses(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "ageGroup", required = false) String ageGroup,
            @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
            @RequestParam(value = "difficultyLevel", required = false) String difficultyLevel,
            @RequestParam(value = "durationDays", required = false) Integer durationDays) {
        try {
            List<Course> courses = courseService.filterCourses(title, ageGroup, categoryIds, difficultyLevel, durationDays);
            List<CourseDTO> courseDTOs = courses.stream()
                    .map(CourseDTO::new)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(courseDTOs, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при фильтрации курсов: " + e.getMessage()));
        }
    }

    @Operation(summary = "Получение курса с деталями", description = "Возвращает курс с его модулями и уроками по ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Курс успешно получен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Курс не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{courseId}")
    public ResponseEntity<?> getCourseWithDetails(@PathVariable Long courseId) {
        try {
            Optional<Course> courseOpt = courseService.getCourseWithDetails(courseId);
            if (courseOpt.isPresent()) {
                CourseDTO courseDTO = new CourseDTO(courseOpt.get());
                return new ResponseEntity<>(courseDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("error", "Курс с ID " + courseId + " не найден"), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при получении курса: " + e.getMessage()));
        }
    }

    @Operation(summary = "Получение заявки на курс", description = "Возвращает заявку на курс по ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Заявка успешно получена"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Заявка не найдена"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
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

    @Operation(summary = "Обновление статуса заявки", description = "Обновляет статус заявки на курс.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Статус заявки успешно обновлён"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Некорректный статус"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Заявка не найдена"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
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

    @Operation(summary = "Получение всех заявок на курс", description = "Возвращает список всех заявок на курс.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Список заявок успешно получен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Курс не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{courseId}/applications/pending")
    public ResponseEntity<List<CourseApplication>> getPendingApplications(
            @PathVariable Long courseId,
            @RequestHeader("email") String specialistEmail) {
        logger.info("Запрос списка ожидающих заявок для курса ID: {} от специалиста: {}", courseId, specialistEmail);
        List<CourseApplication> applications = applicationService.getPendingApplicationsForCourse(courseId);
        return ResponseEntity.ok(applications);
    }

    @Operation(summary = "Завершение урока", description = "Отмечает урок как завершённый для слушателя.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Урок успешно завершён"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Урок уже завершён или не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Урок не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
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

    @Operation(summary = "Прохождение теста", description = "Отмечает тест как пройденный для слушателя.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Тест успешно пройден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Тест уже пройден или не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Тест не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
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

    @Operation(summary = "Получение прогресса слушателя по курсу", description = "Возвращает статистику прогресса слушателя по курсу.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Статистика успешно получена"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Статистика не найдена"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{courseId}/progress")
    public ResponseEntity<?> getProgressStat(
            @PathVariable Long courseId,
            @RequestHeader("userId") Long listenerId) {
        logger.info("Запрос статистики прогресса для курса ID: {} от слушателя ID: {}",
                courseId, listenerId);
        try {
            Optional<ProgressStat> progressOpt = courseService.getProgressStat(listenerId, courseId);
            if (progressOpt.isPresent()) {
                ProgressStatDTO progressDTO = new ProgressStatDTO(progressOpt.get());
                return new ResponseEntity<>(progressDTO, HttpStatus.OK);
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

    @Operation(summary = "Получение прогресса всех курсов слушателя", description = "Возвращает список прогресса всех курсов для слушателя.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Список прогресса успешно получен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Слушатель не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/my-progress")
    public ResponseEntity<?> getAllProgressForListener(
            @RequestHeader("userId") Long listenerId) {
        logger.info("Запрос списка прогресса всех курсов для слушателя ID: {}", listenerId);
        try {
            List<ProgressStat> progressList = courseService.getAllProgressForListener(listenerId);
            List<ProgressStatDTO> progressDTOs = progressList.stream()
                    .map(ProgressStatDTO::new)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(progressDTOs, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Ошибка при получении списка прогресса: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при получении списка прогресса: " + e.getMessage()));
        }
    }

    @Operation(summary = "Получение всех категорий курсов", description = "Возвращает список всех категорий курсов.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Список категорий успешно получен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories() {
        try {
            List<CourseCategory> categories = courseService.getAllCategories();
            List<CourseCategoryDTO> categoryDTOs = categories.stream()
                    .map(CourseCategoryDTO::new)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(categoryDTOs, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при получении списка категорий: " + e.getMessage()));
        }
    }

    @Operation(summary = "Получение курсов специалиста", description = "Возвращает список курсов, созданных специалистом.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Список курсов успешно получен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Специалист не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/specialist")
    public ResponseEntity<?> getCoursesBySpecialist(@RequestHeader("userId") Long specialistId) {
        try {
            logger.info("Запрос списка курсов для специалиста ID: {}", specialistId);
            List<Course> courses = courseService.getCoursesBySpecialistId(specialistId);
            List<CourseDTO> courseDTOs = courses.stream()
                    .map(CourseDTO::new)
                    .collect(Collectors.toList());
            if (courseDTOs.isEmpty()) {
                return new ResponseEntity<>(Map.of("message", "У специалиста нет созданных курсов"), HttpStatus.OK);
            }
            return new ResponseEntity<>(courseDTOs, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Ошибка при получении списка курсов специалиста: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при получении списка курсов: " + e.getMessage()));
        }
    }

    @Operation(summary = "Публикация курса", description = "Публикует курс по его ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Курс успешно опубликован"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Курс не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Ошибка доступа"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PutMapping("/{courseId}/publish")
    public ResponseEntity<?> publishCourse(@PathVariable Long courseId) {
        logger.info("Запрос на публикацию курса ID: {}", courseId);
        try {
            Optional<Course> publishedCourseOpt = courseService.publishCourse(courseId);
            if (publishedCourseOpt.isPresent()) {
                CourseDTO courseDTO = new CourseDTO(publishedCourseOpt.get());
                return new ResponseEntity<>(courseDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("error", "Курс с ID " + courseId + " не найден"), HttpStatus.NOT_FOUND);
            }
        } catch (IllegalStateException e) {
            logger.error("Ошибка при публикации курса: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Ошибка при публикации курса: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при публикации курса: " + e.getMessage()));
        }
    }

    @Operation(summary = "Отмена публикации курса", description = "Отменяет публикацию курса по его ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Курс успешно отменён"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Курс не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Ошибка доступа"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PutMapping("/{courseId}/unpublish")
    public ResponseEntity<?> unpublishCourse(@PathVariable Long courseId) {
        logger.info("Запрос на отмену публикации курса ID: {}", courseId);
        try {
            Optional<Course> unpublishedCourseOpt = courseService.unpublishCourse(courseId);
            if (unpublishedCourseOpt.isPresent()) {
                CourseDTO courseDTO = new CourseDTO(unpublishedCourseOpt.get());
                return new ResponseEntity<>(courseDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("error", "Курс с ID " + courseId + " не найден"), HttpStatus.NOT_FOUND);
            }
        } catch (IllegalStateException e) {
            logger.error("Ошибка при отмене публикации курса: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Ошибка при отмене публикации курса: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при отмене публикации курса: " + e.getMessage()));
        }
    }

    @Operation(summary = "Получение всех опубликованных курсов", description = "Возвращает список всех опубликованных курсов.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Список курсов успешно получен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/published")
    public ResponseEntity<?> getPublishedCourses() {
        try {
            List<Course> publishedCourses = courseService.getPublishedCourses();
            List<CourseDTO> courseDTOs = publishedCourses.stream()
                    .map(CourseDTO::new)
                    .collect(Collectors.toList());
            if (courseDTOs.isEmpty()) {
                return new ResponseEntity<>(Map.of("message", "Нет опубликованных курсов"), HttpStatus.OK);
            }
            return new ResponseEntity<>(courseDTOs, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Ошибка при получении списка опубликованных курсов: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при получении списка опубликованных курсов: " + e.getMessage()));
        }
    }

    @Operation(summary = "Получение всех модулей курса", description = "Возвращает список всех модулей курса по его ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Список модулей успешно получен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Курс не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{courseId}/modules")
    public ResponseEntity<?> getModulesByCourseId(@PathVariable Long courseId) {
        try {
            List<CourseModule> modules = courseService.getModulesByCourseId(courseId);
            List<CourseModuleDTO> moduleDTOs = modules.stream()
                    .map(CourseModuleDTO::new)
                    .collect(Collectors.toList());
            if (moduleDTOs.isEmpty()) {
                return new ResponseEntity<>(Map.of("message", "Модули для курса с ID " + courseId + " не найдены"), HttpStatus.OK);
            }
            return new ResponseEntity<>(moduleDTOs, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Ошибка при получении модулей курса: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при получении модулей: " + e.getMessage()));
        }
    }

    @Operation(summary = "Получение модуля курса", description = "Возвращает модуль курса по его ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Модуль успешно получен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Модуль не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{courseId}/modules/{moduleId}")
    public ResponseEntity<?> getModuleById(@PathVariable Long courseId, @PathVariable Long moduleId) {
        try {
            Optional<CourseModule> moduleOpt = courseService.getModuleById(moduleId);
            if (moduleOpt.isPresent()) {
                CourseModuleDTO moduleDTO = new CourseModuleDTO(moduleOpt.get());
                return new ResponseEntity<>(moduleDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("error", "Модуль с ID " + moduleId + " не найден"), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Ошибка при получении модуля: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при получении модуля: " + e.getMessage()));
        }
    }

    @Operation(summary = "Получение всех уроков модуля", description = "Возвращает список всех уроков модуля по его ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Список уроков успешно получен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Модуль не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{courseId}/modules/{moduleId}/lessons")
    public ResponseEntity<?> getLessonsByModuleId(@PathVariable Long courseId, @PathVariable Long moduleId) {
        try {
            List<Lesson> lessons = courseService.getLessonsByModuleId(moduleId);
            List<LessonDTO> lessonDTOs = lessons.stream()
                    .map(LessonDTO::new)
                    .collect(Collectors.toList());
            if (lessonDTOs.isEmpty()) {
                return new ResponseEntity<>(Map.of("message", "Уроки для модуля с ID " + moduleId + " не найдены"), HttpStatus.OK);
            }
            return new ResponseEntity<>(lessonDTOs, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Ошибка при получении уроков модуля: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при получении уроков: " + e.getMessage()));
        }
    }

    @Operation(summary = "Получение урока модуля", description = "Возвращает урок модуля по его ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Урок успешно получен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Урок не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<?> getLessonById(@PathVariable Long courseId, @PathVariable Long moduleId, @PathVariable Long lessonId) {
        try {
            Optional<Lesson> lessonOpt = courseService.getLessonById(lessonId);
            if (lessonOpt.isPresent()) {
                LessonDTO lessonDTO = new LessonDTO(lessonOpt.get());
                return new ResponseEntity<>(lessonDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("error", "Урок с ID " + lessonId + " не найден"), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Ошибка при получении урока: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при получении урока: " + e.getMessage()));
        }
    }

    @Operation(summary = "Получение всех тестов урока", description = "Возвращает список всех тестов урока по его ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Список тестов успешно получен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Урок не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests")
    public ResponseEntity<?> getTestsByLessonId(@PathVariable Long courseId, @PathVariable Long moduleId, @PathVariable Long lessonId) {
        try {
            List<Test> tests = courseService.getTestsByLessonId(lessonId);
            List<TestDTO> testDTOs = tests.stream()
                    .map(TestDTO::new)
                    .collect(Collectors.toList());
            if (testDTOs.isEmpty()) {
                return new ResponseEntity<>(Map.of("message", "Тесты для урока с ID " + lessonId + " не найдены"), HttpStatus.OK);
            }
            return new ResponseEntity<>(testDTOs, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Ошибка при получении тестов урока: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при получении тестов: " + e.getMessage()));
        }
    }

    @Operation(summary = "Получение теста урока", description = "Возвращает тест урока по его ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Тест успешно получен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Тест не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests/{testId}")
    public ResponseEntity<?> getTestById(@PathVariable Long courseId, @PathVariable Long moduleId, @PathVariable Long lessonId, @PathVariable Long testId) {
        try {
            Optional<Test> testOpt = courseService.getTestById(testId);
            if (testOpt.isPresent()) {
                TestDTO testDTO = new TestDTO(testOpt.get());
                return new ResponseEntity<>(testDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("error", "Тест с ID " + testId + " не найден"), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Ошибка при получении теста: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при получении теста: " + e.getMessage()));
        }
    }

    @Operation(summary = "Получение всех вопросов теста", description = "Возвращает список всех вопросов теста по его ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Список вопросов успешно получен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Тест не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests/{testId}/questions")
    public ResponseEntity<?> getQuestionsByTestId(@PathVariable Long courseId, @PathVariable Long moduleId, @PathVariable Long lessonId, @PathVariable Long testId) {
        try {
            List<Question> questions = courseService.getQuestionsByTestId(testId);
            List<QuestionDTO> questionDTOs = questions.stream()
                    .map(QuestionDTO::new)
                    .collect(Collectors.toList());
            if (questionDTOs.isEmpty()) {
                return new ResponseEntity<>(Map.of("message", "Вопросы для теста с ID " + testId + " не найдены"), HttpStatus.OK);
            }
            return new ResponseEntity<>(questionDTOs, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Ошибка при получении вопросов теста: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при получении вопросов: " + e.getMessage()));
        }
    }

    @Operation(summary = "Получение вопроса теста", description = "Возвращает вопрос теста по его ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Вопрос успешно получен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Вопрос не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests/{testId}/questions/{questionId}")
    public ResponseEntity<?> getQuestionById(@PathVariable Long courseId, @PathVariable Long moduleId, @PathVariable Long lessonId, @PathVariable Long testId, @PathVariable Long questionId) {
        try {
            Optional<Question> questionOpt = courseService.getQuestionById(questionId);
            if (questionOpt.isPresent()) {
                QuestionDTO questionDTO = new QuestionDTO(questionOpt.get());
                return new ResponseEntity<>(questionDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("error", "Вопрос с ID " + questionId + " не найден"), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Ошибка при получении вопроса: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при получении вопроса: " + e.getMessage()));
        }
    }

    @Operation(summary = "Получение всех ответов на вопрос", description = "Возвращает список всех ответов на вопрос по его ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Список ответов успешно получен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Вопрос не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests/{testId}/questions/{questionId}/answers")
    public ResponseEntity<?> getAnswersByQuestionId(@PathVariable Long courseId, @PathVariable Long moduleId, @PathVariable Long lessonId, @PathVariable Long testId, @PathVariable Long questionId) {
        try {
            List<Answer> answers = courseService.getAnswersByQuestionId(questionId);
            List<AnswerDTO> answerDTOs = answers.stream()
                    .map(AnswerDTO::new)
                    .collect(Collectors.toList());
            if (answerDTOs.isEmpty()) {
                return new ResponseEntity<>(Map.of("message", "Ответы для вопроса с ID " + questionId + " не найдены"), HttpStatus.OK);
            }
            return new ResponseEntity<>(answerDTOs, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Ошибка при получении ответов на вопрос: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при получении ответов: " + e.getMessage()));
        }
    }

    @Operation(summary = "Получение ответа на вопрос", description = "Возвращает ответ на вопрос по его ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ответ успешно получен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ответ не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests/{testId}/questions/{questionId}/answers/{answerId}")
    public ResponseEntity<?> getAnswerById(@PathVariable Long courseId, @PathVariable Long moduleId, @PathVariable Long lessonId, @PathVariable Long testId, @PathVariable Long questionId, @PathVariable Long answerId) {
        try {
            Optional<Answer> answerOpt = courseService.getAnswerById(answerId);
            if (answerOpt.isPresent()) {
                AnswerDTO answerDTO = new AnswerDTO(answerOpt.get());
                return new ResponseEntity<>(answerDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("error", "Ответ с ID " + answerId + " не найден"), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Ошибка при получении ответа: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при получении ответа: " + e.getMessage()));
        }
    }
}