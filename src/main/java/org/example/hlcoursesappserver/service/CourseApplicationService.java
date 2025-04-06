package org.example.hlcoursesappserver.service;

import org.example.hlcoursesappserver.model.*;
import org.example.hlcoursesappserver.repository.CourseApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseApplicationService {

    private final CourseApplicationRepository applicationRepository;
    private final ListenerService listenerService;
    private final CourseService courseService;
    private final SpecialistService specialistService;
    private final ProgressStatService progressStatService;

    @Autowired
    public CourseApplicationService(CourseApplicationRepository applicationRepository,
                                    ListenerService listenerService,
                                    CourseService courseService,
                                    SpecialistService specialistService,
                                    ProgressStatService progressStatService) {
        this.applicationRepository = applicationRepository;
        this.listenerService = listenerService;
        this.courseService = courseService;
        this.specialistService = specialistService;
        this.progressStatService = progressStatService;
    }

    @Transactional
    public CourseApplication applyForCourse(Long listenerId, Long courseId) {
        // Проверяем существование слушателя и курса
        Listener listener = listenerService.getUserById(listenerId);
        Course course = courseService.getCourseWithDetails(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Проверяем, не подал ли слушатель заявку ранее
        if (applicationRepository.findByListenerIdAndCourseId(listenerId, courseId).isPresent()) {
            throw new RuntimeException("Заявка на этот курс уже подана");
        }

        // Создаём заявку со статусом APPROVED
        CourseApplication application = new CourseApplication(listenerId, courseId);
        application.setStatus(CourseApplication.ApplicationStatus.APPROVED);
        applicationRepository.save(application);

        // Создаём запись в ProgressStat сразу после одобрения
        progressStatService.createProgressStat(listenerId, courseId);

        return application;
    }

    @Transactional
    public CourseApplication updateApplicationStatus(Long applicationId, String specialistEmail, CourseApplication.ApplicationStatus newStatus) {
        CourseApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        Course course = courseService.getCourseWithDetails(application.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        Specialist specialist = specialistService.getUserById(course.getSpecialistId());

        if (!specialist.getEmail().equals(specialistEmail)) {
            throw new RuntimeException("Only the course specialist can update the application status");
        }

        application.setStatus(newStatus);
        applicationRepository.save(application);

        // Если статус стал APPROVED, создаём запись в ProgressStat (на случай, если позже захотите вернуться к ручному обновлению)
        if (newStatus == CourseApplication.ApplicationStatus.APPROVED) {
            progressStatService.createProgressStat(application.getListenerId(), application.getCourseId());
        }

        return application;
    }

    public List<CourseApplication> getPendingApplicationsForCourse(Long courseId) {
        return applicationRepository.findByCourseIdAndStatus(courseId, CourseApplication.ApplicationStatus.PENDING);
    }

    public List<CourseApplication> getApplicationsByListener(Long listenerId) {
        return applicationRepository.findByListenerId(listenerId);
    }
}