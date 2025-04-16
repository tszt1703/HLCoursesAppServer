package org.example.hlcoursesappserver.service;

import jakarta.transaction.Transactional;
import org.example.hlcoursesappserver.dto.*;
import org.example.hlcoursesappserver.model.*;
import org.example.hlcoursesappserver.repository.*;
import org.hibernate.Hibernate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final CourseCategoryRepository courseCategoryRepository;
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ProgressStatService progressStatService;

    public CourseService(CourseRepository courseRepository,
                         CourseModuleRepository moduleRepository,
                         LessonRepository lessonRepository,
                         CourseCategoryRepository courseCategoryRepository,
                         TestRepository testRepository,
                         QuestionRepository questionRepository,
                         AnswerRepository answerRepository,
                         ProgressStatService progressStatService) {
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
        this.courseCategoryRepository = courseCategoryRepository;
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.progressStatService = progressStatService;
    }

    // Получение списка курсов специалиста
    @Transactional
    public List<Course> getCoursesBySpecialistId(Long specialistId) {
        List<Course> courses = courseRepository.findBySpecialistId(specialistId);
        courses.forEach(course -> {
            Hibernate.initialize(course.getCategories());
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
        });
        return courses;
    }

    // Удаляем метод getCategoryNameById, так как он больше не нужен

    // Создание курса
    public Course createCourse(CourseRequest courseRequest, Long specialistId) {
        // Обрабатываем категории
        List<CourseCategory> categories = new ArrayList<>();
        if (courseRequest.getCategoryNames() != null && !courseRequest.getCategoryNames().isEmpty()) {
            for (String categoryName : courseRequest.getCategoryNames()) {
                // Ищем категорию по имени (игнорируя регистр)
                CourseCategory category = courseCategoryRepository.findByCategoryNameIgnoreCase(categoryName)
                        .orElseGet(() -> {
                            // Если категория не найдена, создаём новую
                            CourseCategory newCategory = new CourseCategory();
                            newCategory.setCategoryName(categoryName);
                            return courseCategoryRepository.save(newCategory);
                        });
                categories.add(category);
            }
        }

        Course course = new Course();
        course.setSpecialistId(specialistId);
        course.setCategories(categories);
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

            // Обновление категорий
            if (updateRequest.getCategoryNames() != null) {
                List<CourseCategory> categories = new ArrayList<>();
                if (!updateRequest.getCategoryNames().isEmpty()) {
                    for (String categoryName : updateRequest.getCategoryNames()) {
                        if (categoryName == null || categoryName.trim().isEmpty()) {
                            continue; // Пропускаем пустые имена
                        }
                        // Ищем категорию по имени (игнорируя регистр)
                        CourseCategory category = courseCategoryRepository.findByCategoryNameIgnoreCase(categoryName.trim())
                                .orElseGet(() -> {
                                    // Если категория не найдена, создаём новую
                                    CourseCategory newCategory = new CourseCategory();
                                    newCategory.setCategoryName(categoryName.trim());
                                    return courseCategoryRepository.save(newCategory);
                                });
                        categories.add(category);
                    }
                }
                course.setCategories(categories);
            }

            // Обновление остальных полей
            if (updateRequest.getTitle() != null) course.setTitle(updateRequest.getTitle());
            if (updateRequest.getShortDescription() != null) course.setShortDescription(updateRequest.getShortDescription());
            if (updateRequest.getFullDescription() != null) course.setFullDescription(updateRequest.getFullDescription());
            if (updateRequest.getDifficultyLevel() != null) course.setDifficultyLevel(updateRequest.getDifficultyLevel());
            if (updateRequest.getAgeGroup() != null) course.setAgeGroup(updateRequest.getAgeGroup());
            if (updateRequest.getDurationDays() != null) course.setDurationDays(updateRequest.getDurationDays());
            if (updateRequest.getPhotoUrl() != null) course.setPhotoUrl(updateRequest.getPhotoUrl());

            Course updatedCourse = courseRepository.save(course);
            Hibernate.initialize(updatedCourse.getModules());
            Hibernate.initialize(updatedCourse.getCategories());
            return Optional.of(updatedCourse);
        }
        return Optional.empty();
    }

    @Transactional
    public CourseModule createModule(Long courseId, ModuleRequest moduleRequest) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            throw new DataIntegrityViolationException("Курс с ID " + courseId + " не существует");
        }
        Course course = courseOpt.get();

        CourseModule module = new CourseModule();
        module.setCourseId(courseId);
        module.setTitle(moduleRequest.getTitle());
        module.setDescription(moduleRequest.getDescription());

        List<CourseModule> existingModules = moduleRepository.findByCourseId(courseId);
        int newPosition = 1;
        if (existingModules != null && !existingModules.isEmpty()) {
            newPosition = existingModules.stream()
                    .mapToInt(CourseModule::getPosition)
                    .max()
                    .orElse(0) + 1;
        }
        module.setPosition(newPosition);

        return moduleRepository.save(module);
    }

    @Transactional
    public Lesson createLesson(Long moduleId, LessonRequest lessonRequest) {
        Optional<CourseModule> moduleOpt = moduleRepository.findById(moduleId);
        if (moduleOpt.isEmpty()) {
            throw new DataIntegrityViolationException("Модуль с ID " + moduleId + " не существует");
        }

        Lesson lesson = new Lesson();
        lesson.setModuleId(moduleId);
        lesson.setTitle(lessonRequest.getTitle());
        lesson.setContent(lessonRequest.getContent());
        lesson.setPhotoUrl(lessonRequest.getPhotoUrl());
        lesson.setVideoUrl(lessonRequest.getVideoUrl());

        List<Lesson> existingLessons = lessonRepository.findByModuleId(moduleId);
        int newPosition = 1;
        if (existingLessons != null && !existingLessons.isEmpty()) {
            newPosition = existingLessons.stream()
                    .mapToInt(Lesson::getPosition)
                    .max()
                    .orElse(0) + 1;
        }
        lesson.setPosition(newPosition);

        return lessonRepository.save(lesson);
    }

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

    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);
    }

    @Transactional
    public void deleteModule(Long moduleId) {
        Optional<CourseModule> moduleOpt = moduleRepository.findById(moduleId);
        if (moduleOpt.isEmpty()) {
            throw new IllegalArgumentException("Модуль с ID " + moduleId + " не найден");
        }
        moduleRepository.deleteById(moduleId);
    }

    @Transactional
    public void deleteLesson(Long lessonId) {
        Optional<Lesson> lessonOpt = lessonRepository.findById(lessonId);
        if (lessonOpt.isEmpty()) {
            throw new IllegalArgumentException("Урок с ID " + lessonId + " не найден");
        }
        lessonRepository.deleteById(lessonId);
    }

    @Transactional
    public void deleteTest(Long testId) {
        Optional<Test> testOpt = testRepository.findById(testId);
        if (testOpt.isEmpty()) {
            throw new IllegalArgumentException("Тест с ID " + testId + " не найден");
        }
        testRepository.deleteById(testId);
    }

    @Transactional
    public void deleteQuestion(Long questionId) {
        Optional<Question> questionOpt = questionRepository.findById(questionId);
        if (questionOpt.isEmpty()) {
            throw new IllegalArgumentException("Вопрос с ID " + questionId + " не найден");
        }
        questionRepository.deleteById(questionId);
    }

    @Transactional
    public void deleteAnswer(Long answerId) {
        Optional<Answer> answerOpt = answerRepository.findById(answerId);
        if (answerOpt.isEmpty()) {
            throw new IllegalArgumentException("Ответ с ID " + answerId + " не найден");
        }
        answerRepository.deleteById(answerId);
    }

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

    public List<Course> searchCoursesByTitle(String title) {
        return courseRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Course> filterCourses(String title, String ageGroup, List<Long> categoryIds, String difficultyLevel, Integer durationDays) {
        return courseRepository.findCoursesByFilters(title, ageGroup, categoryIds, difficultyLevel, durationDays);
    }

    @Transactional
    public Optional<Course> getCourseWithDetails(Long courseId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();
            Hibernate.initialize(course.getCategories());
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

    public Optional<ProgressStat> getProgressStat(Long listenerId, Long courseId) {
        return progressStatService.getProgressStat(listenerId, courseId);
    }

    public List<ProgressStat> getAllProgressForListener(Long listenerId) {
        return progressStatService.getAllProgressForListener(listenerId);
    }

    public List<CourseCategory> getAllCategories() {
        return courseCategoryRepository.findAll();
    }

    @Transactional
    public Optional<Course> publishCourse(Long courseId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();
            course.setStatus("published");
            Course publishedCourse = courseRepository.save(course);
            return Optional.of(publishedCourse);
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<Course> unpublishCourse(Long courseId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();
            if (course.getStatus().equals("draft")) {
                throw new IllegalStateException("Курс уже находится в статусе черновика");
            }
            course.setStatus("draft");
            Course unpublishedCourse = courseRepository.save(course);
            return Optional.of(unpublishedCourse);
        }
        return Optional.empty();
    }

    @Transactional
    public List<Course> getPublishedCourses() {
        List<Course> courses = courseRepository.findByStatus("published");
        courses.forEach(course -> {
            Hibernate.initialize(course.getCategories());
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
        });
        return courses;
    }

    @Transactional
    public List<CourseModule> getModulesByCourseId(Long courseId) {
        return moduleRepository.findByCourseId(courseId);
    }

    @Transactional
    public Optional<CourseModule> getModuleById(Long moduleId) {
        return moduleRepository.findById(moduleId);
    }

    @Transactional
    public List<Lesson> getLessonsByModuleId(Long moduleId) {
        return lessonRepository.findByModuleId(moduleId);
    }

    @Transactional
    public Optional<Lesson> getLessonById(Long lessonId) {
        return lessonRepository.findById(lessonId);
    }

    @Transactional
    public List<Test> getTestsByLessonId(Long lessonId) {
        return testRepository.findByLessonId(lessonId);
    }

    @Transactional
    public Optional<Test> getTestById(Long testId) {
        return testRepository.findById(testId);
    }

    @Transactional
    public List<Question> getQuestionsByTestId(Long testId) {
        return questionRepository.findByTestId(testId);
    }

    @Transactional
    public Optional<Question> getQuestionById(Long questionId) {
        return questionRepository.findById(questionId);
    }

    @Transactional
    public List<Answer> getAnswersByQuestionId(Long questionId) {
        return answerRepository.findByQuestionId(questionId);
    }

    @Transactional
    public Optional<Answer> getAnswerById(Long answerId) {
        return answerRepository.findById(answerId);
    }
}