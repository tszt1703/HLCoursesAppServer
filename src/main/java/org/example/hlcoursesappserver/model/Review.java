package org.example.hlcoursesappserver.model;

import jakarta.persistence.*;

// Таблица для отзывов
@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    private Long listenerId;

    private Long courseId;

    private Integer rating;

    private String reviewText;
    // Getters and Setters

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public Long getListenerId() {
        return listenerId;
    }

    public void setListenerId(Long listenerId) {
        this.listenerId = listenerId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public Review() {
    }

    public Review(Long listenerId, Long courseId, Integer rating, String reviewText) {
        this.listenerId = listenerId;
        this.courseId = courseId;
        this.rating = rating;
        this.reviewText = reviewText;
    }
}
