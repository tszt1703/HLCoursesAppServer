package org.example.hlcoursesappserver.repository;

import org.example.hlcoursesappserver.model.CourseApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseApplicationRepository extends JpaRepository<CourseApplication, Long> {
    List<CourseApplication> findByCourseIdAndStatus(Long courseId, CourseApplication.ApplicationStatus status);
    List<CourseApplication> findByListenerId(Long listenerId);
    Optional<CourseApplication> findByListenerIdAndCourseId(Long listenerId, Long courseId);
}