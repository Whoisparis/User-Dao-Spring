package org.example.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceApplicationTest {

    @Test
    void testContextLoads() {
        assertTrue(true, "Context should load successfully");
    }

    @Test
    void testMainMethod() {
        assertDoesNotThrow(() -> UserServiceApplication.main(new String[]{}));
    }
}