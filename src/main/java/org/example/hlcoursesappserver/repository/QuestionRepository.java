package org.example.hlcoursesappserver.repository;

import org.example.hlcoursesappserver.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
