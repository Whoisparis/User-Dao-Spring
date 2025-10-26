package org.example.userservice.repository;

import org.example.userservice.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveUser() {
        User user = new User("John Doe", "john@example.com", 30);

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals("John Doe", savedUser.getName());
        assertEquals("john@example.com", savedUser.getEmail());
        assertEquals(30, savedUser.getAge());
        assertNotNull(savedUser.getCreatedAt());
    }

    @Test
    void testFindById() {
        User user = new User("John Doe", "john@example.com", 30);
        User savedUser = userRepository.save(user);

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
        assertEquals("John Doe", foundUser.get().getName());
        assertEquals("john@example.com", foundUser.get().getEmail());
    }

    @Test
    void testFindById_NotFound() {
        Optional<User> foundUser = userRepository.findById(999L);

        assertFalse(foundUser.isPresent());
    }

    @Test
    void testFindAll() {
        User user1 = new User("John Doe", "john@example.com", 30);
        User user2 = new User("Jane Smith", "jane@example.com", 25);
        userRepository.save(user1);
        userRepository.save(user2);

        List<User> users = userRepository.findAll();

        assertEquals(2, users.size());
    }

    @Test
    void testFindByEmail() {

        User user = new User("John Doe", "find@example.com", 30);
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmail("find@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("John Doe", foundUser.get().getName());
        assertEquals("find@example.com", foundUser.get().getEmail());
    }

    @Test
    void testFindByEmail_NotFound() {

        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        assertFalse(foundUser.isPresent());
    }

    @Test
    void testExistsByEmail() {
        User user = new User("John Doe", "exists@example.com", 30);
        userRepository.save(user);

        assertTrue(userRepository.existsByEmail("exists@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    void testExistsByEmailAndIdNot() {
        User user1 = new User("John Doe", "test@example.com", 30);
        User user2 = new User("Jane Smith", "jane@example.com", 25);
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        assertTrue(userRepository.existsByEmailAndIdNot("test@example.com", savedUser2.getId()));
        assertFalse(userRepository.existsByEmailAndIdNot("test@example.com", savedUser1.getId()));
        assertFalse(userRepository.existsByEmailAndIdNot("nonexistent@example.com", savedUser1.getId()));
    }

    @Test
    void testEmailUniqueConstraint() {
        User user1 = new User("John Doe", "duplicate@example.com", 30);
        userRepository.save(user1);

        User user2 = new User("Jane Smith", "duplicate@example.com", 25);

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(user2);
        });
    }

    @Test
    void testUpdateUser() {
        User user = new User("John Doe", "update@example.com", 30);
        User savedUser = userRepository.save(user);

        savedUser.setName("John Updated");
        savedUser.setAge(35);
        User updatedUser = userRepository.save(savedUser);

        assertEquals("John Updated", updatedUser.getName());
        assertEquals(35, updatedUser.getAge());
        assertEquals(savedUser.getId(), updatedUser.getId());
        assertEquals("update@example.com", updatedUser.getEmail());
    }

    @Test
    void testDeleteUser() {
        User user = new User("John Doe", "delete@example.com", 30);
        User savedUser = userRepository.save(user);

        userRepository.delete(savedUser);
        Optional<User> deletedUser = userRepository.findById(savedUser.getId());

        assertFalse(deletedUser.isPresent());
    }

    @Test
    void testCreatedAtIsAutomaticallySet() {
        User user = new User("Test User", "created@example.com", 30);
        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getCreatedAt());
    }

    @Test
    void testFindByEmailCaseSensitive() {
        User user = new User("John Doe", "CaseSensitive@Example.com", 30);
        userRepository.save(user);

        assertTrue(userRepository.existsByEmail("CaseSensitive@Example.com"));
        assertTrue(userRepository.findByEmail("CaseSensitive@Example.com").isPresent());
    }
}