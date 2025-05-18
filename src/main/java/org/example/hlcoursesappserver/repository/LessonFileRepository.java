package org.example.hlcoursesappserver.repository;

import org.example.hlcoursesappserver.model.LessonFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonFileRepository extends JpaRepository<LessonFile, Long> {
    List<LessonFile> findByLesson_LessonId(Long lessonId);
}