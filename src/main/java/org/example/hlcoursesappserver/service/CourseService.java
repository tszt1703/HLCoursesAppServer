package org.example.hlcoursesappserver.service;

import org.example.hlcoursesappserver.dto.CourseRequest;
import org.example.hlcoursesappserver.dto.CourseUpdateRequest;
import org.example.hlcoursesappserver.dto.ModuleRequest;
import org.example.hlcoursesappserver.dto.LessonRequest;
import org.example.hlcoursesappserver.model.Course;
import org.example.hlcoursesappserver.model.CourseModule;
import org.example.hlcoursesappserver.model.Lesson;
import org.example.hlcoursesappserver.repository.CourseRepository;
import org.example.hlcoursesappserver.repository.CourseModuleRepository;
import org.example.hlcoursesappserver.repository.LessonRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;

    public CourseService(CourseRepository courseRepository,
                         CourseModuleRepository moduleRepository,
                         LessonRepository lessonRepository) {
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
    }

    /**
     * Создает новый курс.
     */
    public Course createCourse(CourseRequest courseRequest, Long specialistId) {
        Course course = new Course();
        course.setSpecialistId(specialistId);
        course.setCategoryId(courseRequest.getCategoryId());
        course.setTitle(courseRequest.getTitle());
        course.setShortDescription(courseRequest.getShortDescription());
        course.setFullDescription(courseRequest.getFullDescription());
        course.setDifficultyLevel(courseRequest.getDifficultyLevel());
        course.setAgeGroup(courseRequest.getAgeGroup());
        course.setDurationDays(courseRequest.getDurationDays());
        course.setPhotoUrl(courseRequest.getPhotoUrl());
        course.setStatus("draft");
        return courseRepository.save(course);
    }

    /**
     * Обновляет существующий курс.
     */
    public Optional<Course> updateCourse(Long courseId, CourseUpdateRequest updateRequest) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();
            // Обновляем необходимые поля
            course.setCategoryId(updateRequest.getCategoryId());
            course.setTitle(updateRequest.getTitle());
            course.setShortDescription(updateRequest.getShortDescription());
            course.setFullDescription(updateRequest.getFullDescription());
            course.setDifficultyLevel(updateRequest.getDifficultyLevel());
            course.setAgeGroup(updateRequest.getAgeGroup());
            course.setDurationDays(updateRequest.getDurationDays());
            course.setPhotoUrl(updateRequest.getPhotoUrl());
            return Optional.of(courseRepository.save(course));
        }
        return Optional.empty();
    }

    /**
     * Создает новый модуль для курса.
     */
    public CourseModule createModule(Long courseId, ModuleRequest moduleRequest) {
        CourseModule module = new CourseModule();
        module.setCourseId(courseId);
        module.setTitle(moduleRequest.getTitle());
        module.setDescription(moduleRequest.getDescription());
        module.setPosition(moduleRequest.getPosition());
        return moduleRepository.save(module);
    }

    /**
     * Создает новый урок для модуля.
     */
    public Lesson createLesson(Long moduleId, LessonRequest lessonRequest) {
        Lesson lesson = new Lesson();
        lesson.setModuleId(moduleId);
        lesson.setTitle(lessonRequest.getTitle());
        lesson.setContent(lessonRequest.getContent());
        lesson.setPhotoUrl(lessonRequest.getPhotoUrl());
        lesson.setVideoUrl(lessonRequest.getVideoUrl());
        lesson.setPosition(lessonRequest.getPosition());
        return lessonRepository.save(lesson);
    }
}
