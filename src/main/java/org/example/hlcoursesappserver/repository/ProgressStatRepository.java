package org.example.hlcoursesappserver.repository;

import org.example.hlcoursesappserver.model.ProgressStat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgressStatRepository extends JpaRepository<ProgressStat, Long> {
}
