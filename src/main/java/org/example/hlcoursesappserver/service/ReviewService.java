package org.example.hlcoursesappserver.service;

import org.example.hlcoursesappserver.dto.ReviewDTO;
import org.example.hlcoursesappserver.model.Course;
import org.example.hlcoursesappserver.model.Listener;
import org.example.hlcoursesappserver.model.Review;
import org.example.hlcoursesappserver.repository.CourseRepository;
import org.example.hlcoursesappserver.repository.ListenerRepository;
import org.example.hlcoursesappserver.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

//    private final ReviewRepository reviewRepository;
//    private final ListenerRepository listenerRepository;
//    private final CourseRepository courseRepository;
//
//    @Autowired
//    public ReviewService(ReviewRepository reviewRepository, ListenerRepository listenerRepository, CourseRepository courseRepository) {
//        this.reviewRepository = reviewRepository;
//        this.listenerRepository = listenerRepository;
//        this.courseRepository = courseRepository;
//    }
//
//    public Review addReview(ReviewDTO reviewDTO) {
//        Review review = new Review();
//        Listener listener = listenerRepository.findById(reviewDTO.getListenerId())
//                .orElseThrow(() -> new IllegalArgumentException("Listener not found"));
//        Course course = courseRepository.findById(reviewDTO.getCourseId())
//                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
//
//        review.setListener(listener);
//        review.setCourse(course);
//        review.setContent(reviewDTO.getContent());
//        review.setRating(reviewDTO.getRating());
//        review.setTimestamp(reviewDTO.getTimestamp());
//
//        return reviewRepository.save(review);
//    }
//
//    public Optional<Review> getReviewById(Long id) {
//        return reviewRepository.findById(id);
//    }
//
//    public List<Review> getAllReviews() {
//        return reviewRepository.findAll();
//    }
//
//    public void deleteReview(Long id) {
//        reviewRepository.deleteById(id);
//    }
}
