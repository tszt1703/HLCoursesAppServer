package org.example.hlcoursesappserver.controller;

import org.example.hlcoursesappserver.dto.ReviewDTO;
import org.example.hlcoursesappserver.model.Review;
import org.example.hlcoursesappserver.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
//
//    private final ReviewService reviewService;
//
//    @Autowired
//    public ReviewController(ReviewService reviewService) {
//        this.reviewService = reviewService;
//    }
//
//    @PostMapping
//    public Review addReview(@RequestBody ReviewDTO reviewDTO) {
//        return reviewService.addReview(reviewDTO);
//    }
//
//    @GetMapping("/{id}")
//    public Review getReviewById(@PathVariable Long id) {
//        return reviewService.getReviewById(id).orElse(null);
//    }
//
//    @GetMapping
//    public List<Review> getAllReviews() {
//        return reviewService.getAllReviews();
//    }
//
//    @DeleteMapping("/{id}")
//    public void deleteReview(@PathVariable Long id) {
//        reviewService.deleteReview(id);
//    }
}
