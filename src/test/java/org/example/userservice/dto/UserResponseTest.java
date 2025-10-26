package org.example.userservice.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class UserResponseTest {

    private UserResponse userResponse;
    private final LocalDateTime testDateTime = LocalDateTime.of(2025, 10, 26, 10, 30);

    @BeforeEach
    void setUp() {
        userResponse = new UserResponse();
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(userResponse);
        assertNull(userResponse.getId());
        assertNull(userResponse.getName());
        assertNull(userResponse.getEmail());
        assertNull(userResponse.getAge());
        assertNull(userResponse.getCreatedAt());
    }

    @Test
    void testParameterizedConstructor() {
        UserResponse response = new UserResponse(1L, "John Doe", "john@example.com", 30, testDateTime);

        assertEquals(1L, response.getId());
        assertEquals("John Doe", response.getName());
        assertEquals("john@example.com", response.getEmail());
        assertEquals(30, response.getAge());
        assertEquals(testDateTime, response.getCreatedAt());
    }

    @Test
    void testSettersAndGetters() {
        Long id = 2L;
        String name = "Jane Smith";
        String email = "jane@example.com";
        Integer age = 25;
        LocalDateTime createdAt = LocalDateTime.now();

        userResponse.setId(id);
        userResponse.setName(name);
        userResponse.setEmail(email);
        userResponse.setAge(age);
        userResponse.setCreatedAt(createdAt);

        assertEquals(id, userResponse.getId());
        assertEquals(name, userResponse.getName());
        assertEquals(email, userResponse.getEmail());
        assertEquals(age, userResponse.getAge());
        assertEquals(createdAt, userResponse.getCreatedAt());
    }

    @Test
    void testAllFieldsCanBeNull() {
        userResponse.setId(null);
        userResponse.setName(null);
        userResponse.setEmail(null);
        userResponse.setAge(null);
        userResponse.setCreatedAt(null);

        assertNull(userResponse.getId());
        assertNull(userResponse.getName());
        assertNull(userResponse.getEmail());
        assertNull(userResponse.getAge());
        assertNull(userResponse.getCreatedAt());
    }

    @Test
    void testEdgeCaseValues() {
        Long maxId = Long.MAX_VALUE;
        String longName = "A".repeat(100);
        String complexEmail = "test.user+tag@sub.domain.co.uk";
        Integer zeroAge = 0;
        Integer maxAge = 150;

        userResponse.setId(maxId);
        userResponse.setName(longName);
        userResponse.setEmail(complexEmail);
        userResponse.setAge(zeroAge);

        assertEquals(maxId, userResponse.getId());
        assertEquals(longName, userResponse.getName());
        assertEquals(complexEmail, userResponse.getEmail());
        assertEquals(zeroAge, userResponse.getAge());

        userResponse.setAge(maxAge);
        assertEquals(maxAge, userResponse.getAge());
    }
}