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

    // Фильтрация по категории
    List<Course> findByCategoryId(Long categoryId);

    // Фильтрация по уровню сложности
    List<Course> findByDifficultyLevel(String difficultyLevel);

    // Фильтрация по времени прохождения
    List<Course> findByDurationDays(Integer durationDays);

    List<Course> findBySpecialistId(Long specialistId);

    List<Course> findByStatus(String status);

    // Комбинированная фильтрация с необязательными параметрами
    @Query("SELECT c FROM Course c WHERE " +
            "(:title IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:ageGroup IS NULL OR c.ageGroup = :ageGroup) AND " +
            "(:categoryId IS NULL OR c.categoryId = :categoryId) AND " +
            "(:difficultyLevel IS NULL OR c.difficultyLevel = :difficultyLevel) AND " +
            "(:durationDays IS NULL OR c.durationDays = :durationDays)")
    List<Course> findCoursesByFilters(
            @Param("title") String title,
            @Param("ageGroup") String ageGroup,
            @Param("categoryId") Long categoryId,
            @Param("difficultyLevel") String difficultyLevel,
            @Param("durationDays") Integer durationDays);
}