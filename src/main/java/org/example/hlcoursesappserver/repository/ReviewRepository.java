package org.example.hlcoursesappserver.repository;

import org.example.hlcoursesappserver.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByListenerListenerIdAndCourseCourseId(Long listenerId, Long courseId);
    boolean existsByListenerListenerIdAndCourseCourseId(Long listenerId, Long courseId);
    List<Review> findByCourseCourseId(Long courseId);
}