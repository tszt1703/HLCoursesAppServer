package org.example.hlcoursesappserver.repository;

import org.example.hlcoursesappserver.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Define custom queries if needed
}
