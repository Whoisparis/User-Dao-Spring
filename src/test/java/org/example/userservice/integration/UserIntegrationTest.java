package org.example.userservice.integration;

import org.example.userservice.entity.User;
import org.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void createAndRetrieveUser_ShouldWork() throws Exception {
        String userJson = """
            {
                "name": "Integration Test User",
                "email": "integration@test.com",
                "age": 25
            }
            """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Integration Test User")))
                .andExpect(jsonPath("$.email", is("integration@test.com")))
                .andExpect(jsonPath("$.age", is(25)));

        assertEquals(1, userRepository.count());
    }

    @Test
    void createUserWithDuplicateEmail_ShouldReturnConflict() throws Exception {
        User user = new User("First User", "duplicate@test.com", 30);
        userRepository.save(user);

        String userJson = """
            {
                "name": "Second User",
                "email": "duplicate@test.com",
                "age": 25
            }
            """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }
}
