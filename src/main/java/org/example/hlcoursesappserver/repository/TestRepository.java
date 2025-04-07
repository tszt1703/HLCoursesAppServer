package org.example.hlcoursesappserver.repository;

import org.example.hlcoursesappserver.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findByLessonId(Long lessonId);
}
