package org.example.hlcoursesappserver.repository;

import org.example.hlcoursesappserver.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
}
