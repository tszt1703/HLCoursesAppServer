package org.example.hlcoursesappserver.repository;

import org.example.hlcoursesappserver.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
