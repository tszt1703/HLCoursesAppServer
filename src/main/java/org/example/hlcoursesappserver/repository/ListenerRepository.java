package org.example.hlcoursesappserver.repository;

import org.example.hlcoursesappserver.model.Listener;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ListenerRepository extends JpaRepository<Listener, Long> {
    Optional<Listener> findByEmail(String email);
    boolean existsByEmail(String email);
}