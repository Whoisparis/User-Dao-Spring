package org.example.userservice.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleUserNotFound_ShouldReturnNotFoundResponse() {
        UserNotFoundException exception = new UserNotFoundException(1L);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleUserNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("not found"));
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleEmailAlreadyExists_ShouldReturnConflictResponse() {
        EmailAlreadyExistsException exception = new EmailAlreadyExistsException("test@example.com");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleEmailAlreadyExists(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("already exists"));
    }

    @Test
    void handleValidationExceptions_ShouldReturnBadRequestWithErrors() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError("userRequest", "name", "Name is required");
        FieldError fieldError2 = new FieldError("userRequest", "email", "Email is invalid");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        ResponseEntity<Map<String, String>> response =
                exceptionHandler.handleValidationExceptions(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("Name is required", response.getBody().get("name"));
        assertEquals("Email is invalid", response.getBody().get("email"));
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerError() {
        Exception exception = new RuntimeException("Unexpected error");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
    }
}
