package org.example.hlcoursesappserver.repository;

import org.example.hlcoursesappserver.model.ProgressStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgressStatRepository extends JpaRepository<ProgressStat, Long> {
    Optional<ProgressStat> findByListenerIdAndCourseId(Long listenerId, Long courseId);
    List<ProgressStat> findByListenerId(Long listenerId);
}
