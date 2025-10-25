package org.example.userservice.service;

import org.example.userservice.dto.UserRequest;
import org.example.userservice.dto.UserResponse;
import org.example.userservice.entity.User;
import org.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_WithValidData_ShouldReturnUserResponse() {
        // Arrange
        UserRequest userRequest = new UserRequest();
        userRequest.setName("John Doe");
        userRequest.setEmail("john@example.com");
        userRequest.setAge(30);

        User user = new User("John Doe", "john@example.com", 30);
        user.setId(1L);
        user.setCreatedAt(LocalDateTime.now());

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserResponse result = userService.createUser(userRequest);

        // Assert
        assertNotNull(result);
        verify(userRepository).existsByEmail("john@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_WithExistingEmail_ShouldThrowException() {
        // Arrange
        UserRequest userRequest = new UserRequest();
        userRequest.setName("John Doe");
        userRequest.setEmail("existing@example.com");
        userRequest.setAge(30);

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // Act & Assert
        assertThrows(org.example.userservice.exception.EmailAlreadyExistsException.class,
                () -> userService.createUser(userRequest));

        verify(userRepository).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUserResponse() {
        // Arrange
        User user = new User("John Doe", "john@example.com", 30);
        user.setId(1L);
        user.setCreatedAt(LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        Object result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_WhenUserNotExists_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(org.example.userservice.exception.UserNotFoundException.class,
                () -> userService.getUserById(999L));
        verify(userRepository).findById(999L);
    }

    @Test
    void getAllUsers_ShouldReturnUserResponseList() {
        // Arrange
        User user1 = new User("User 1", "user1@example.com", 25);
        user1.setId(1L);
        User user2 = new User("User 2", "user2@example.com", 30);
        user2.setId(2L);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // Act
        List<?> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void updateUser_WithValidData_ShouldReturnUpdatedUser() {
        // Arrange
        User existingUser = new User("Old Name", "old@example.com", 25);
        existingUser.setId(1L);

        UserRequest updateRequest = new UserRequest();
        updateRequest.setName("New Name");
        updateRequest.setEmail("new@example.com");
        updateRequest.setAge(30);

        User updatedUser = new User("New Name", "new@example.com", 30);
        updatedUser.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailAndIdNot("new@example.com", 1L)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        UserResponse result = userService.updateUser(1L, updateRequest); // обратите внимание на добавление ID

        // Assert
        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmailAndIdNot("new@example.com", 1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteUser() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void getUserByEmail_WhenUserExists_ShouldReturnUserResponse() {
        // Arrange
        User user = new User("John Doe", "john@example.com", 30);
        user.setId(1L);
        user.setCreatedAt(LocalDateTime.now());

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        // Act
        Object result = userService.getUserByEmail("john@example.com");

        // Assert
        assertNotNull(result);
        verify(userRepository).findByEmail("john@example.com");
    }

    // HELPER METHOD
    private Object createUserRequest(String name, String email, Integer age) {
        return new Object() {
            public String getName() { return name; }
            public String getEmail() { return email; }
            public Integer getAge() { return age; }
        };
    }
}

