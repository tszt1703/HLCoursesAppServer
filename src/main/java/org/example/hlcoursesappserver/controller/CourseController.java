package org.example.hlcoursesappserver.controller;

import org.example.hlcoursesappserver.dto.CourseDTO;
import org.example.hlcoursesappserver.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

//    private final CourseService courseService;
//
//    @Autowired
//    public CourseController(CourseService courseService) {
//        this.courseService = courseService;
//    }
//
//    @GetMapping
//    public ResponseEntity<List<CourseDTO>> getAllCourses() {
//        return ResponseEntity.ok(courseService.getAllCourses());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
//        return ResponseEntity.ok(courseService.getCourseById(id));
//    }
//
//    @PostMapping
//    public ResponseEntity<CourseDTO> createCourse(@RequestBody CourseDTO courseDTO) {
//        return ResponseEntity.ok(courseService.createCourse(courseDTO));
//    }
}
