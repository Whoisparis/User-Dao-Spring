package org.example.userservice.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class UserRequestTest {

    private UserRequest userRequest;
    private static Validator validator;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest();

        if (validator == null) {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            validator = factory.getValidator();
        }
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(userRequest);
        assertNull(userRequest.getName());
        assertNull(userRequest.getEmail());
        assertNull(userRequest.getAge());
    }

    @Test
    void testParameterizedConstructor() {
        UserRequest request = new UserRequest("John Doe", "john@example.com", 30);

        assertEquals("John Doe", request.getName());
        assertEquals("john@example.com", request.getEmail());
        assertEquals(30, request.getAge());
    }

    @Test
    void testSettersAndGetters() {
        String name = "Jane Smith";
        String email = "jane@example.com";
        Integer age = 25;

        userRequest.setName(name);
        userRequest.setEmail(email);
        userRequest.setAge(age);

        assertEquals(name, userRequest.getName());
        assertEquals(email, userRequest.getEmail());
        assertEquals(age, userRequest.getAge());
    }

    @Test
    void testValidUserRequest() {
        userRequest.setName("Valid User");
        userRequest.setEmail("valid@example.com");
        userRequest.setAge(30);

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);

        assertTrue(violations.isEmpty(), "No validation violations should occur for valid data");
    }

    @Test
    void testBlankNameValidation() {
        userRequest.setName("");
        userRequest.setEmail("valid@example.com");
        userRequest.setAge(30);

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Name cannot be blank")));
    }

    @Test
    void testNullNameValidation() {
        userRequest.setName(null);
        userRequest.setEmail("valid@example.com");
        userRequest.setAge(30);

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Name cannot be blank")));
    }

    @Test
    void testInvalidEmailValidation() {
        userRequest.setName("Valid User");
        userRequest.setEmail("invalid-email");
        userRequest.setAge(30);

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Email should be valid")));
    }

    @Test
    void testNegativeAgeValidation() {
        userRequest.setName("Valid User");
        userRequest.setEmail("valid@example.com");
        userRequest.setAge(-5);

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Age cannot be negative")));
    }

    @Test
    void testNullAgeIsAllowed() {
        userRequest.setName("Valid User");
        userRequest.setEmail("valid@example.com");
        userRequest.setAge(null);

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);

        assertTrue(violations.isEmpty(), "Null age should be allowed");
    }

    @Test
    void testZeroAgeIsAllowed() {
        userRequest.setName("Valid User");
        userRequest.setEmail("valid@example.com");
        userRequest.setAge(0);

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);

        assertTrue(violations.isEmpty(), "Zero age should be allowed");
    }
}