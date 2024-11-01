package org.example.hlcoursesappserver.controller;

import org.example.hlcoursesappserver.model.Lesson;
import org.example.hlcoursesappserver.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lessons")
public class LessonController {

//    private final LessonService lessonService;
//
//    @Autowired
//    public LessonController(LessonService lessonService) {
//        this.lessonService = lessonService;
//    }
//
//    @GetMapping
//    public ResponseEntity<List<Lesson>> getAllLessons() {
//        return ResponseEntity.ok(lessonService.getAllLessons());
//    }
//
//    @PostMapping
//    public ResponseEntity<Lesson> createLesson(@RequestBody Lesson lesson) {
//        return ResponseEntity.ok(lessonService.createLesson(lesson));
//    }
}
