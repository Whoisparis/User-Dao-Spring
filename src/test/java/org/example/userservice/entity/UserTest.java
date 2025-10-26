package org.example.userservice.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("John Doe", "john@example.com", 30);
    }

    @Test
    void testDefaultConstructor() {
        User defaultUser = new User();

        assertNotNull(defaultUser);
        assertNull(defaultUser.getId());
        assertNull(defaultUser.getName());
        assertNull(defaultUser.getEmail());
        assertNull(defaultUser.getAge());
        assertNotNull(defaultUser.getCreatedAt());
    }

    @Test
    void testParameterizedConstructor() {
        User user = new User("John Doe", "john@example.com", 30);

        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals(30, user.getAge());
        assertNotNull(user.getCreatedAt());
        assertNull(user.getId());
    }

    @Test
    void testSettersAndGetters() {
        Long id = 1L;
        String name = "Jane Smith";
        String email = "jane@example.com";
        Integer age = 25;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);

        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);
        user.setCreatedAt(createdAt);

        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(age, user.getAge());
        assertEquals(createdAt, user.getCreatedAt());
    }

    @Test
    void testCreatedAtAutomaticallySet() {
        User newUser = new User();

        assertNotNull(newUser.getCreatedAt());
        assertTrue(newUser.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(newUser.getCreatedAt().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    void testCreatedAtInParameterizedConstructor() {
        User user = new User("Test User", "test@example.com", 40);

        assertNotNull(user.getCreatedAt());
        assertTrue(user.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testAgeCanBeNull() {
        User userWithNullAge = new User("Test User", "test@example.com", null);

        assertNull(userWithNullAge.getAge());
    }

    @Test
    void testNameNotNullConstraint() {
        assertDoesNotThrow(() -> user.setName("Valid Name"));
    }

    @Test
    void testEmailNotNullConstraint() {
        assertDoesNotThrow(() -> user.setEmail("valid@email.com"));
    }
}

