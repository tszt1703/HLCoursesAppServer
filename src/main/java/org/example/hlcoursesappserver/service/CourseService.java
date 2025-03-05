package org.example.hlcoursesappserver.service;

import org.example.hlcoursesappserver.dto.*;
import org.example.hlcoursesappserver.model.*;
import org.example.hlcoursesappserver.repository.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final CourseCategoryRepository courseCategoryRepository;
    private final TestRepository testRepository; // Новый репозиторий
    private final QuestionRepository questionRepository; // Новый репозиторий
    private final AnswerRepository answerRepository; // Новый репозиторий

    public CourseService(CourseRepository courseRepository,
                         CourseModuleRepository moduleRepository,
                         LessonRepository lessonRepository,
                         CourseCategoryRepository courseCategoryRepository,
                         TestRepository testRepository,
                         QuestionRepository questionRepository,
                         AnswerRepository answerRepository) {
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
        this.courseCategoryRepository = courseCategoryRepository;
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    // Существующие методы остаются без изменений
    public Course createCourse(CourseRequest courseRequest, Long specialistId) {
        Optional<CourseCategory> categoryOpt = courseCategoryRepository.findByCategoryName(courseRequest.getCategoryName());
        CourseCategory category;

        if (categoryOpt.isPresent()) {
            category = categoryOpt.get();
        } else {
            category = new CourseCategory(courseRequest.getCategoryName());
            category = courseCategoryRepository.save(category);
        }

        Course course = new Course();
        course.setSpecialistId(specialistId);
        course.setCategoryId(category.getCategoryId());
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

    public Optional<Course> updateCourse(Long courseId, CourseUpdateRequest updateRequest) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();
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

    public CourseModule createModule(Long courseId, ModuleRequest moduleRequest) {
        CourseModule module = new CourseModule();
        module.setCourseId(courseId);
        module.setTitle(moduleRequest.getTitle());
        module.setDescription(moduleRequest.getDescription());
        module.setPosition(moduleRequest.getPosition());
        return moduleRepository.save(module);
    }

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

    // Новые методы для тестов
    public Test createTest(Long lessonId, TestRequest testRequest) {
        Test test = new Test();
        test.setLessonId(lessonId);
        test.setTitle(testRequest.getTitle());
        return testRepository.save(test);
    }

    public Question createQuestion(Long testId, QuestionRequest questionRequest) {
        Question question = new Question();
        question.setTestId(testId);
        question.setQuestionText(questionRequest.getQuestionText());
        return questionRepository.save(question);
    }

    public Answer createAnswer(Long questionId, AnswerRequest answerRequest) {
        Answer answer = new Answer();
        answer.setQuestionId(questionId);
        answer.setAnswerText(answerRequest.getAnswerText());
        answer.setCorrect(answerRequest.getIsCorrect());
        return answerRepository.save(answer);
    }

    // Метод для удаления курса
    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId); // Каскадное удаление сработает автоматически
    }
}