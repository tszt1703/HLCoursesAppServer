package org.example.hlcoursesappserver.service;

import org.example.hlcoursesappserver.model.ProgressStat;
import org.example.hlcoursesappserver.repository.ProgressStatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProgressStatService {

    private final ProgressStatRepository progressStatRepository;

    // Убираем зависимость от CourseService
    public ProgressStatService(ProgressStatRepository progressStatRepository) {
        this.progressStatRepository = progressStatRepository;
    }

    @Transactional
    public ProgressStat createProgressStat(Long listenerId, Long courseId) {
        ProgressStat progress = new ProgressStat(listenerId, courseId);
        progress.setLessonsCompleted(0);
        progress.setTestsPassed(0);
        progress.setProgressPercent(0.0f);
        return progressStatRepository.save(progress);
    }

    @Transactional
    public Optional<ProgressStat> updateLessonCompleted(Long listenerId, Long courseId, Long lessonId, int totalLessons, int totalTests) {
        Optional<ProgressStat> progressOpt = progressStatRepository.findByListenerIdAndCourseId(listenerId, courseId);
        if (progressOpt.isPresent()) {
            ProgressStat progress = progressOpt.get();
            progress.setLessonsCompleted(progress.getLessonsCompleted() + 1);
            updateProgressPercent(progress, totalLessons, totalTests);
            return Optional.of(progressStatRepository.save(progress));
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<ProgressStat> updateTestPassed(Long listenerId, Long courseId, Long testId, int totalLessons, int totalTests) {
        Optional<ProgressStat> progressOpt = progressStatRepository.findByListenerIdAndCourseId(listenerId, courseId);
        if (progressOpt.isPresent()) {
            ProgressStat progress = progressOpt.get();
            progress.setTestsPassed(progress.getTestsPassed() + 1);
            updateProgressPercent(progress, totalLessons, totalTests);
            return Optional.of(progressStatRepository.save(progress));
        }
        return Optional.empty();
    }

    private void updateProgressPercent(ProgressStat progress, int totalLessons, int totalTests) {
        float lessonProgress = totalLessons > 0 ?
                (progress.getLessonsCompleted() * 50.0f) / totalLessons : 0;
        float testProgress = totalTests > 0 ?
                (progress.getTestsPassed() * 50.0f) / totalTests : 0;
        progress.setProgressPercent(lessonProgress + testProgress);
    }

    public Optional<ProgressStat> getProgressStat(Long listenerId, Long courseId) {
        return progressStatRepository.findByListenerIdAndCourseId(listenerId, courseId);
    }

    public List<ProgressStat> getAllProgressForListener(Long listenerId) {
        return progressStatRepository.findByListenerId(listenerId);
    }
}