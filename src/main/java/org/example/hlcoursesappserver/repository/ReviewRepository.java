package org.example.hlcoursesappserver.repository;

import org.example.hlcoursesappserver.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Define custom queries if needed
}
