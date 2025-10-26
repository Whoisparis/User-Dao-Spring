package org.example.userservice.repository;

import org.example.userservice.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testUserPersistenceIntegration() {
        User user = new User("Integration User", "integration@test.com", 40);

        User savedUser = userRepository.save(user);
        User foundUser = userRepository.findById(savedUser.getId()).orElse(null);

        assertNotNull(foundUser);
        assertEquals(savedUser.getId(), foundUser.getId());
        assertEquals("Integration User", foundUser.getName());
        assertEquals("integration@test.com", foundUser.getEmail());
        assertEquals(40, foundUser.getAge());
        assertNotNull(foundUser.getCreatedAt());
    }

    @Test
    void testMultipleOperations() {
        User user1 = new User("User One", "one@test.com", 25);
        User user2 = new User("User Two", "two@test.com", 30);

        userRepository.save(user1);
        userRepository.save(user2);

        List<User> users = userRepository.findAll();
        assertEquals(2, users.size());
        assertTrue(userRepository.existsByEmail("one@test.com"));
        assertTrue(userRepository.existsByEmail("two@test.com"));
    }
}