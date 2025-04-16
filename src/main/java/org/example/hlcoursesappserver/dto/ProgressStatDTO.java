package org.example.hlcoursesappserver.dto;

import org.example.hlcoursesappserver.model.ProgressStat;

import java.util.List;

public class ProgressStatDTO {
    private Long listenerId;
    private Long courseId;
    private List<Long> completedLessons;
    private List<Long> passedTests;
    private Integer totalLessons;
    private Integer totalTests;

    public ProgressStatDTO(ProgressStat progressStat) {
        this.listenerId = progressStat.getListenerId();
        this.courseId = progressStat.getCourseId();
//        this.completedLessons = progressStat.getCompletedLessons();
//        this.passedTests = progressStat.getPassedTests();
//        this.totalLessons = progressStat.getTotalLessons();
//        this.totalTests = progressStat.getTotalTests();
    }

    // Getters and setters
    public Long getListenerId() { return listenerId; }
    public void setListenerId(Long listenerId) { this.listenerId = listenerId; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public List<Long> getCompletedLessons() { return completedLessons; }
    public void setCompletedLessons(List<Long> completedLessons) { this.completedLessons = completedLessons; }
    public List<Long> getPassedTests() { return passedTests; }
    public void setPassedTests(List<Long> passedTests) { this.passedTests = passedTests; }
    public Integer getTotalLessons() { return totalLessons; }
    public void setTotalLessons(Integer totalLessons) { this.totalLessons = totalLessons; }
    public Integer getTotalTests() { return totalTests; }
    public void setTotalTests(Integer totalTests) { this.totalTests = totalTests; }
}