package org.example.hlcoursesappserver.service;

import org.example.hlcoursesappserver.dto.CourseRequest;
import org.example.hlcoursesappserver.model.Course;
import org.example.hlcoursesappserver.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    /**
     * Создает новый курс по данным, полученным из CourseRequest.
     *
     * @param courseRequest данные для создания курса
     * @param specialistId  идентификатор специалиста, создающего курс
     * @return созданный объект Course
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
        // Статус будет установлен в "draft" согласно настройкам в сущности

        return courseRepository.save(course);
    }
}
