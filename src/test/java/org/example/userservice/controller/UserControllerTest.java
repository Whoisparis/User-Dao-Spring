package org.example.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void createUser_WithValidData_ShouldReturnCreated() throws Exception {
        String userJson = """
            {
                "name": "John Doe",
                "email": "john@example.com",
                "age": 30
            }
            """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated());

        verify(userService).createUser(any());
    }

    @Test
    void createUser_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        String invalidUserJson = """
            {
                "name": "",
                "email": "invalid-email",
                "age": -5
            }
            """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidUserJson))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any());
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() throws Exception {

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk());

        verify(userService).getUserById(1L);
    }

    @Test
    void getUserById_WhenUserNotExists_ShouldReturnNotFound() throws Exception {
        when(userService.getUserById(999L))
                .thenThrow(new org.example.userservice.exception.UserNotFoundException(999L));

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("not found")));

        verify(userService).getUserById(999L);
    }

    @Test
    void getAllUsers_ShouldReturnUsersList() throws Exception {

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());

        verify(userService).getAllUsers();
    }

    @Test
    void getAllUsers_WhenNoUsers_ShouldReturnEmptyList() throws Exception {

        when(userService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(userService).getAllUsers();
    }

    @Test
    void updateUser_WithValidData_ShouldReturnUpdatedUser() throws Exception {

        String updateJson = """
            {
                "name": "John Updated",
                "email": "updated@example.com",
                "age": 35
            }
            """;

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk());

        verify(userService).updateUser(eq(1L), any());
    }

    @Test
    void updateUser_WhenUserNotExists_ShouldReturnNotFound() throws Exception {

        String updateJson = """
            {
                "name": "John Updated",
                "email": "updated@example.com",
                "age": 35
            }
            """;

        when(userService.updateUser(eq(999L), any()))
                .thenThrow(new org.example.userservice.exception.UserNotFoundException(999L));

        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isNotFound());

        verify(userService).updateUser(eq(999L), any());
    }

    @Test
    void updateUser_WithExistingEmail_ShouldReturnConflict() throws Exception {

        String updateJson = """
            {
                "name": "John Updated",
                "email": "existing@example.com",
                "age": 35
            }
            """;

        when(userService.updateUser(eq(1L), any()))
                .thenThrow(new org.example.userservice.exception.EmailAlreadyExistsException("existing@example.com"));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isConflict());

        verify(userService).updateUser(eq(1L), any());
    }

    @Test
    void deleteUser_WhenUserExists_ShouldReturnNoContent() throws Exception {

        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    void deleteUser_WhenUserNotExists_ShouldReturnNotFound() throws Exception {

        doThrow(new org.example.userservice.exception.UserNotFoundException(999L))
                .when(userService).deleteUser(999L);

        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(userService).deleteUser(999L);
    }

    @Test
    void getUserByEmail_WhenUserExists_ShouldReturnUser() throws Exception {

        mockMvc.perform(get("/api/users/email/john@example.com"))
                .andExpect(status().isOk());

        verify(userService).getUserByEmail("john@example.com");
    }

    @Test
    void getUserByEmail_WhenUserNotExists_ShouldReturnNotFound() throws Exception {

        when(userService.getUserByEmail("nonexistent@example.com"))
                .thenThrow(new org.example.userservice.exception.UserNotFoundException("User not found with email: nonexistent@example.com"));

        mockMvc.perform(get("/api/users/email/nonexistent@example.com"))
                .andExpect(status().isNotFound());

        verify(userService).getUserByEmail("nonexistent@example.com");
    }

    @Test
    void createUser_WithExistingEmail_ShouldReturnConflict() throws Exception {
        String userJson = """
            {
                "name": "John Doe",
                "email": "existing@example.com",
                "age": 30
            }
            """;

        when(userService.createUser(any()))
                .thenThrow(new org.example.userservice.exception.EmailAlreadyExistsException("existing@example.com"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.message", containsString("already exists")));

        verify(userService).createUser(any());
    }
}