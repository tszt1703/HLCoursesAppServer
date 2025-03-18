package org.example.hlcoursesappserver.config;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ValidationConfigTest {

    @Autowired
    private Validator validator;

    static class TestEntity {
        @jakarta.validation.constraints.NotBlank
        private String name;

        TestEntity(String name) {
            this.name = name;
        }

        public String getName() { return name; }
    }

    @Test
    void testValidatorNotBlankConstraint() {
        TestEntity validEntity = new TestEntity("test");
        TestEntity invalidEntity = new TestEntity("");

        Set<ConstraintViolation<TestEntity>> validViolations = validator.validate(validEntity);
        Set<ConstraintViolation<TestEntity>> invalidViolations = validator.validate(invalidEntity);

        assertTrue(validViolations.isEmpty(), "Валидная сущность не должна иметь нарушений");
        assertFalse(invalidViolations.isEmpty(), "Невалидная сущность должна иметь нарушения");
    }
}