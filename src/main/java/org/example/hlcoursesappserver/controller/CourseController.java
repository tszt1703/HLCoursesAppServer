package org.example.hlcoursesappserver.controller;

import org.example.hlcoursesappserver.dto.CourseRequest;
import org.example.hlcoursesappserver.dto.CourseUpdateRequest;
import org.example.hlcoursesappserver.dto.ModuleRequest;
import org.example.hlcoursesappserver.dto.LessonRequest;
import org.example.hlcoursesappserver.model.Course;
import org.example.hlcoursesappserver.model.CourseModule;
import org.example.hlcoursesappserver.model.Lesson;
import org.example.hlcoursesappserver.service.CourseService;
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
     */
    @PostMapping
    public ResponseEntity<Course> createCourse(@Valid @RequestBody CourseRequest courseRequest) {
        Long specialistId = courseRequest.getSpecialistId();
        Course createdCourse = courseService.createCourse(courseRequest, specialistId);
        return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
    }

    /**
     * Эндпоинт для обновления курса.
     */
    @PutMapping("/{courseId}")
    public ResponseEntity<?> updateCourse(@PathVariable Long courseId,
                                          @Valid @RequestBody CourseUpdateRequest updateRequest) {
        Optional<Course> updatedCourseOpt = courseService.updateCourse(courseId, updateRequest);
        if (updatedCourseOpt.isPresent()) {
            return new ResponseEntity<>(updatedCourseOpt.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Курс с идентификатором " + courseId + " не найден", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Эндпоинт для создания модуля в курсе.
     */
    @PostMapping("/{courseId}/modules")
    public ResponseEntity<CourseModule> createModule(@PathVariable Long courseId,
                                                     @Valid @RequestBody ModuleRequest moduleRequest) {
        CourseModule createdModule = courseService.createModule(courseId, moduleRequest);
        return new ResponseEntity<>(createdModule, HttpStatus.CREATED);
    }

    /**
     * Эндпоинт для создания урока в модуле.
     * Здесь для однозначности URL включаем courseId, хотя для создания урока достаточно moduleId.
     */
    @PostMapping("/{courseId}/modules/{moduleId}/lessons")
    public ResponseEntity<Lesson> createLesson(@PathVariable Long moduleId,
                                               @Valid @RequestBody LessonRequest lessonRequest) {
        Lesson createdLesson = courseService.createLesson(moduleId, lessonRequest);
        return new ResponseEntity<>(createdLesson, HttpStatus.CREATED);
    }
}
