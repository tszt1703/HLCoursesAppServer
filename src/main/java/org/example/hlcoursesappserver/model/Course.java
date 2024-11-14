package org.example.hlcoursesappserver.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "specialist_id", nullable = false)
    private Specialist specialist;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CourseCategory category;

    private String title;
    private String description;
    private String difficulty_level;
    private String age_group;
    private int duration_days;
    private String plan;
    private String photo_url;
    private String video_url;
    private boolean certificate_available;
    private LocalDateTime created_at;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Lesson> lessons;

    // Getters and setters

    public Long get_id() {
        return id;
    }

    public void set_id(Long course_id) {
        this.id = course_id;
    }

    public Specialist getSpecialist() {
        return specialist;
    }

    public void setSpecialist(Specialist specialist) {
        this.specialist = specialist;
    }

    public CourseCategory getCategory() {
        return category;
    }

    public void setCategory(CourseCategory category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficulty_level() {
        return difficulty_level;
    }

    public void setDifficulty_level(String difficulty_level) {
        this.difficulty_level = difficulty_level;
    }

    public String getAge_group() {
        return age_group;
    }

    public void setAge_group(String age_group) {
        this.age_group = age_group;
    }

    public int getDuration_days() {
        return duration_days;
    }

    public void setDuration_days(int duration_days) {
        this.duration_days = duration_days;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public boolean isCertificate_available() {
        return certificate_available;
    }

    public void setCertificate_available(boolean certificate_available) {
        this.certificate_available = certificate_available;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    @Override
    public String toString() {
        return "Course{" +
                "course_id=" + id +
                ", specialist=" + specialist +
                ", category=" + category +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", difficulty_level='" + difficulty_level + '\'' +
                ", age_group='" + age_group + '\'' +
                ", duration_days=" + duration_days +
                ", plan='" + plan + '\'' +
                ", photo_url='" + photo_url + '\'' +
                ", video_url='" + video_url + '\'' +
                ", certificate_available=" + certificate_available +
                ", created_at=" + created_at +
                '}';
    }

    public Course() {
    }

    public Course(Long id, Specialist specialist, CourseCategory category, String title, String description, String difficulty_level, String age_group, int duration_days, String plan, String photo_url, String video_url, boolean certificate_available, LocalDateTime created_at, List<Lesson> lessons) {
        this.id = id;
        this.specialist = specialist;
        this.category = category;
        this.title = title;
        this.description = description;
        this.difficulty_level = difficulty_level;
        this.age_group = age_group;
        this.duration_days = duration_days;
        this.plan = plan;
        this.photo_url = photo_url;
        this.video_url = video_url;
        this.certificate_available = certificate_available;
        this.created_at = created_at;
        this.lessons = lessons;
    }


}
