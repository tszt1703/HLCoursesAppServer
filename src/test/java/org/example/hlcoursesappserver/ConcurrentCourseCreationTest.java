package org.example.hlcoursesappserver;

import org.example.hlcoursesappserver.model.Course;
import org.example.hlcoursesappserver.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ConcurrentCourseCreationTest {

    @Autowired
    private CourseRepository courseRepository;

    @Test
    public void testConcurrentCourseCreation() throws InterruptedException {
        int numberOfThreads = 10; // Количество специалистов
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        List<String> courseTitles = new ArrayList<>();
        for (int i = 1; i <= numberOfThreads; i++) {
            courseTitles.add("Course " + i);
        }

        for (String title : courseTitles) {
            executorService.submit(() -> {
                Course course = new Course();
                course.setTitle(title);
                course.setStatus("draft");
                courseRepository.save(course);
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        // Проверяем, что все курсы созданы
        List<Course> courses = courseRepository.findAll();
        assertEquals(numberOfThreads, courses.size());
    }
}