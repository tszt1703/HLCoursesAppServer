package org.example.hlcoursesappserver.controller;

import org.example.hlcoursesappserver.dto.CourseRequest;
import org.example.hlcoursesappserver.model.Course;
import org.example.hlcoursesappserver.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/courses")
@Validated
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * Эндпоинт для создания нового курса.
     * Идентификатор специалиста передается с клиентской стороны.
     *
     * @param courseRequest входящие данные для создания курса
     * @return созданный курс с HTTP-статусом 201 (Created)
     */
    @PostMapping
    public ResponseEntity<Course> createCourse(@Valid @RequestBody CourseRequest courseRequest) {
        Long specialistId = courseRequest.getSpecialistId();
        Course createdCourse = courseService.createCourse(courseRequest, specialistId);
        return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
    }
}
