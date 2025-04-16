package org.example.hlcoursesappserver.repository;

import org.example.hlcoursesappserver.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // Поиск по названию (частичное совпадение)
    List<Course> findByTitleContainingIgnoreCase(String title);

    // Фильтрация по возрасту
    List<Course> findByAgeGroup(String ageGroup);

    // Фильтрация по уровню сложности
    List<Course> findByDifficultyLevel(String difficultyLevel);

    // Фильтрация по времени прохождения
    List<Course> findByDurationDays(Integer durationDays);

    List<Course> findBySpecialistId(Long specialistId);

    List<Course> findByStatus(String status);

    @Query("SELECT c FROM Course c " +
            "WHERE (:title IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
            "AND (:ageGroup IS NULL OR c.ageGroup = :ageGroup) " +
            "AND (:difficultyLevel IS NULL OR c.difficultyLevel = :difficultyLevel) " +
            "AND (:durationDays IS NULL OR c.durationDays = :durationDays) " +
            "AND (:categoryIds IS NULL OR EXISTS (SELECT 1 FROM c.categories cat WHERE cat.categoryId IN :categoryIds))")
    List<Course> findCoursesByFilters(
            @Param("title") String title,
            @Param("ageGroup") String ageGroup,
            @Param("categoryIds") List<Long> categoryIds,
            @Param("difficultyLevel") String difficultyLevel,
            @Param("durationDays") Integer durationDays);
}