package org.example.hlcoursesappserver.service;

import org.example.hlcoursesappserver.dto.ReviewRequestDTO;
import org.example.hlcoursesappserver.dto.ReviewResponseDTO;
import org.example.hlcoursesappserver.model.Review;
import org.example.hlcoursesappserver.repository.CourseRepository;
import org.example.hlcoursesappserver.repository.ListenerRepository;
import org.example.hlcoursesappserver.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ListenerRepository listenerRepository;
    private final CourseRepository courseRepository;

    public ReviewService(ReviewRepository reviewRepository, ListenerRepository listenerRepository, CourseRepository courseRepository) {
        this.reviewRepository = reviewRepository;
        this.listenerRepository = listenerRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public ReviewResponseDTO createReview(Long listenerId, ReviewRequestDTO request) {
        if (reviewRepository.existsByListenerListenerIdAndCourseCourseId(listenerId, request.getCourseId())) {
            throw new IllegalArgumentException("Отзыв для этого курса уже существует");
        }
        Review review = new Review();
        review.setListener(listenerRepository.findById(listenerId)
                .orElseThrow(() -> new IllegalArgumentException("Слушатель не найден")));
        review.setCourse(courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Курс не найден")));
        review.setRating(request.getRating());
        review.setReviewText(request.getReviewText());
        Review savedReview = reviewRepository.save(review);
        return mapToResponseDTO(savedReview);
    }

    @Transactional
    public ReviewResponseDTO updateReview(Long listenerId, Long reviewId, ReviewRequestDTO request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Отзыв не найден"));
        if (!review.getListener().getListenerId().equals(listenerId)) {
            throw new IllegalArgumentException("Вы не можете редактировать чужой отзыв");
        }
        review.setRating(request.getRating());
        review.setReviewText(request.getReviewText());
        Review updatedReview = reviewRepository.save(review);
        return mapToResponseDTO(updatedReview);
    }

    @Transactional
    public void deleteReview(Long listenerId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Отзыв не найден"));
        if (!review.getListener().getListenerId().equals(listenerId)) {
            throw new IllegalArgumentException("Вы не можете удалить чужой отзыв");
        }
        reviewRepository.delete(review);
    }

    public Optional<ReviewResponseDTO> getReviewByListenerAndCourse(Long listenerId, Long courseId) {
        return reviewRepository.findByListenerListenerIdAndCourseCourseId(listenerId, courseId)
                .map(this::mapToResponseDTO);
    }

    public Optional<ReviewResponseDTO> getReviewByIdAndListener(Long listenerId, Long reviewId) {
        return reviewRepository.findById(reviewId)
                .filter(review -> review.getListener().getListenerId().equals(listenerId))
                .map(this::mapToResponseDTO);
    }

    public List<ReviewResponseDTO> getAllReviewsByCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new IllegalArgumentException("Курс не найден");
        }
        return reviewRepository.findByCourseCourseId(courseId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private ReviewResponseDTO mapToResponseDTO(Review review) {
        ReviewResponseDTO response = new ReviewResponseDTO();
        response.setReviewId(review.getReviewId());
        response.setListenerId(review.getListener().getListenerId());
        response.setCourseId(review.getCourse().getCourseId());
        response.setRating(review.getRating());
        response.setReviewText(review.getReviewText());
        response.setCreatedAt(review.getCreatedAt());
        return response;
    }
}