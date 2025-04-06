package org.example.hlcoursesappserver.service;

import jakarta.transaction.Transactional;
import org.example.hlcoursesappserver.dto.*;
import org.example.hlcoursesappserver.model.*;
import org.example.hlcoursesappserver.repository.*;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.List;
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
    private final ProgressStatService progressStatService;

    public CourseService(CourseRepository courseRepository,
                         CourseModuleRepository moduleRepository,
                         LessonRepository lessonRepository,
                         CourseCategoryRepository courseCategoryRepository,
                         TestRepository testRepository,
                         QuestionRepository questionRepository,
                         AnswerRepository answerRepository, ProgressStatService progressStatService) {
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
        this.courseCategoryRepository = courseCategoryRepository;
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.progressStatService = progressStatService;
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

    @Transactional
    public Optional<Course> updateCourse(Long courseId, CourseUpdateRequest updateRequest) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();

            // Проверка и обновление категории
            if (updateRequest.getCategoryName() != null) {
                Optional<CourseCategory> categoryOpt = courseCategoryRepository.findByCategoryName(updateRequest.getCategoryName());
                CourseCategory category;
                if (categoryOpt.isPresent()) {
                    category = categoryOpt.get();
                } else {
                    category = new CourseCategory(updateRequest.getCategoryName());
                    category = courseCategoryRepository.save(category);
                }
                course.setCategoryId(category.getCategoryId());
            }

            // Обновление остальных полей
            if (updateRequest.getTitle() != null) course.setTitle(updateRequest.getTitle());
            if (updateRequest.getShortDescription() != null) course.setShortDescription(updateRequest.getShortDescription());
            if (updateRequest.getFullDescription() != null) course.setFullDescription(updateRequest.getFullDescription());
            if (updateRequest.getDifficultyLevel() != null) course.setDifficultyLevel(updateRequest.getDifficultyLevel());
            if (updateRequest.getAgeGroup() != null) course.setAgeGroup(updateRequest.getAgeGroup());
            if (updateRequest.getDurationDays() != null) course.setDurationDays(updateRequest.getDurationDays());
            if (updateRequest.getPhotoUrl() != null) course.setPhotoUrl(updateRequest.getPhotoUrl());

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

    // Новые методы для удаления отдельных сущностей
    @Transactional
    public void deleteModule(Long moduleId) {
        // Проверяем существование модуля
        Optional<CourseModule> moduleOpt = moduleRepository.findById(moduleId);
        if (moduleOpt.isEmpty()) {
            throw new IllegalArgumentException("Модуль с ID " + moduleId + " не найден");
        }
        moduleRepository.deleteById(moduleId); // Каскадное удаление уроков, тестов, вопросов и ответов
    }

    @Transactional
    public void deleteLesson(Long lessonId) {
        // Проверяем существование урока
        Optional<Lesson> lessonOpt = lessonRepository.findById(lessonId);
        if (lessonOpt.isEmpty()) {
            throw new IllegalArgumentException("Урок с ID " + lessonId + " не найден");
        }
        lessonRepository.deleteById(lessonId); // Каскадное удаление тестов, вопросов и ответов
    }

    @Transactional
    public void deleteTest(Long testId) {
        // Проверяем существование теста
        Optional<Test> testOpt = testRepository.findById(testId);
        if (testOpt.isEmpty()) {
            throw new IllegalArgumentException("Тест с ID " + testId + " не найден");
        }
        testRepository.deleteById(testId); // Каскадное удаление вопросов и ответов
    }

    @Transactional
    public void deleteQuestion(Long questionId) {
        // Проверяем существование вопроса
        Optional<Question> questionOpt = questionRepository.findById(questionId);
        if (questionOpt.isEmpty()) {
            throw new IllegalArgumentException("Вопрос с ID " + questionId + " не найден");
        }
        questionRepository.deleteById(questionId); // Каскадное удаление ответов
    }

    @Transactional
    public void deleteAnswer(Long answerId) {
        // Проверяем существование ответа
        Optional<Answer> answerOpt = answerRepository.findById(answerId);
        if (answerOpt.isEmpty()) {
            throw new IllegalArgumentException("Ответ с ID " + answerId + " не найден");
        }
        answerRepository.deleteById(answerId); // Просто удаляем ответ
    }

    // Новые методы для обновления
    @Transactional
    public Optional<CourseModule> updateModule(Long moduleId, ModuleUpdateRequest updateRequest) {
        Optional<CourseModule> moduleOpt = moduleRepository.findById(moduleId);
        if (moduleOpt.isPresent()) {
            CourseModule module = moduleOpt.get();
            module.setTitle(updateRequest.getTitle());
            if (updateRequest.getDescription() != null) module.setDescription(updateRequest.getDescription());
            if (updateRequest.getPosition() != null) module.setPosition(updateRequest.getPosition());
            return Optional.of(moduleRepository.save(module));
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<Lesson> updateLesson(Long lessonId, LessonUpdateRequest updateRequest) {
        Optional<Lesson> lessonOpt = lessonRepository.findById(lessonId);
        if (lessonOpt.isPresent()) {
            Lesson lesson = lessonOpt.get();
            lesson.setTitle(updateRequest.getTitle());
            if (updateRequest.getContent() != null) lesson.setContent(updateRequest.getContent());
            if (updateRequest.getPhotoUrl() != null) lesson.setPhotoUrl(updateRequest.getPhotoUrl());
            if (updateRequest.getVideoUrl() != null) lesson.setVideoUrl(updateRequest.getVideoUrl());
            if (updateRequest.getPosition() != null) lesson.setPosition(updateRequest.getPosition());
            return Optional.of(lessonRepository.save(lesson));
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<Test> updateTest(Long testId, TestUpdateRequest updateRequest) {
        Optional<Test> testOpt = testRepository.findById(testId);
        if (testOpt.isPresent()) {
            Test test = testOpt.get();
            test.setTitle(updateRequest.getTitle());
            return Optional.of(testRepository.save(test));
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<Question> updateQuestion(Long questionId, QuestionUpdateRequest updateRequest) {
        Optional<Question> questionOpt = questionRepository.findById(questionId);
        if (questionOpt.isPresent()) {
            Question question = questionOpt.get();
            question.setQuestionText(updateRequest.getQuestionText());
            return Optional.of(questionRepository.save(question));
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<Answer> updateAnswer(Long answerId, AnswerUpdateRequest updateRequest) {
        Optional<Answer> answerOpt = answerRepository.findById(answerId);
        if (answerOpt.isPresent()) {
            Answer answer = answerOpt.get();
            answer.setAnswerText(updateRequest.getAnswerText());
            answer.setCorrect(updateRequest.getIsCorrect());
            return Optional.of(answerRepository.save(answer));
        }
        return Optional.empty();
    }

    // Новые методы для поиска и фильтрации
    public List<Course> searchCoursesByTitle(String title) {
        return courseRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Course> filterCourses(String title, String ageGroup, Long categoryId, String difficultyLevel, Integer durationDays) {
        return courseRepository.findCoursesByFilters(title, ageGroup, categoryId, difficultyLevel, durationDays);
    }

    // Новый метод для получения полной информации о курсе
    @Transactional
    public Optional<Course> getCourseWithDetails(Long courseId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();
            Hibernate.initialize(course.getModules());
            if (course.getModules() != null) {
                course.getModules().forEach(module -> {
                    Hibernate.initialize(module.getLessons());
                    if (module.getLessons() != null) {
                        module.getLessons().forEach(lesson -> {
                            Hibernate.initialize(lesson.getTests());
                            if (lesson.getTests() != null) {
                                lesson.getTests().forEach(test -> {
                                    Hibernate.initialize(test.getQuestions());
                                    if (test.getQuestions() != null) {
                                        test.getQuestions().forEach(question -> Hibernate.initialize(question.getAnswers()));
                                    }
                                });
                            }
                        });
                    }
                });
            }
            return Optional.of(course);
        }
        return Optional.empty();
    }

    @Transactional
    public void completeLesson(Long listenerId, Long courseId, Long lessonId) {
        Optional<ProgressStat> progressOpt = progressStatService.getProgressStat(listenerId, courseId);
        if (progressOpt.isEmpty()) {
            throw new IllegalStateException("Слушатель не записан на этот курс");
        }

        Optional<Lesson> lessonOpt = lessonRepository.findById(lessonId);
        if (lessonOpt.isEmpty()) {
            throw new IllegalArgumentException("Урок не найден");
        }

        Optional<Course> courseOpt = getCourseWithDetails(courseId);
        if (courseOpt.isEmpty()) {
            throw new IllegalArgumentException("Курс не найден");
        }

        Course course = courseOpt.get();
        int totalLessons = course.getModules().stream()
                .mapToInt(module -> module.getLessons().size())
                .sum();
        int totalTests = course.getModules().stream()
                .flatMap(module -> module.getLessons().stream())
                .mapToInt(lesson -> lesson.getTests().size())
                .sum();

        progressStatService.updateLessonCompleted(listenerId, courseId, lessonId, totalLessons, totalTests);
    }

    @Transactional
    public void passTest(Long listenerId, Long courseId, Long testId) {
        Optional<ProgressStat> progressOpt = progressStatService.getProgressStat(listenerId, courseId);
        if (progressOpt.isEmpty()) {
            throw new IllegalStateException("Слушатель не записан на этот курс");
        }

        Optional<Test> testOpt = testRepository.findById(testId);
        if (testOpt.isEmpty()) {
            throw new IllegalArgumentException("Тест не найден");
        }

        Optional<Course> courseOpt = getCourseWithDetails(courseId);
        if (courseOpt.isEmpty()) {
            throw new IllegalArgumentException("Курс не найден");
        }

        Course course = courseOpt.get();
        int totalLessons = course.getModules().stream()
                .mapToInt(module -> module.getLessons().size())
                .sum();
        int totalTests = course.getModules().stream()
                .flatMap(module -> module.getLessons().stream())
                .mapToInt(lesson -> lesson.getTests().size())
                .sum();

        progressStatService.updateTestPassed(listenerId, courseId, testId, totalLessons, totalTests);
    }

    // Делегирующие методы
    public Optional<ProgressStat> getProgressStat(Long listenerId, Long courseId) {
        return progressStatService.getProgressStat(listenerId, courseId);
    }

    public List<ProgressStat> getAllProgressForListener(Long listenerId) {
        return progressStatService.getAllProgressForListener(listenerId);
    }
}