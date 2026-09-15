package com.corebank.infrastructure.web.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleException_Returns500ProblemDetail() {
        Exception ex = new Exception("Unexpected failure");
        
        ProblemDetail problemDetail = exceptionHandler.handleException(ex);
        
        assertNotNull(problemDetail);
        assertEquals(500, problemDetail.getStatus());
        assertEquals("Internal Server Error", problemDetail.getTitle());
        assertEquals("An unexpected error occurred.", problemDetail.getDetail());
    }

    @Test
    void handleMethodArgumentNotValid_Returns400ProblemDetail() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("objectName", "field1", "must not be null")
        ));
        
        ProblemDetail problemDetail = exceptionHandler.handleMethodArgumentNotValid(ex);
        
        assertNotNull(problemDetail);
        assertEquals(400, problemDetail.getStatus());
        assertEquals("Bad Request", problemDetail.getTitle());
        assertEquals("Invalid request content.", problemDetail.getDetail());
        
        @SuppressWarnings("unchecked")
        List<Object> invalidParams = (List<Object>) problemDetail.getProperties().get("invalid_params");
        assertNotNull(invalidParams);
        assertEquals(1, invalidParams.size());
    }

    @Test
    void handleResourceNotFound_Returns404ProblemDetail() {
        com.corebank.domain.exception.ResourceNotFoundException ex = 
            new com.corebank.domain.exception.ResourceNotFoundException("Account 123 not found");
            
        ProblemDetail problemDetail = exceptionHandler.handleResourceNotFound(ex);
        
        assertNotNull(problemDetail);
        assertEquals(404, problemDetail.getStatus());
        assertEquals("Not Found", problemDetail.getTitle());
        assertEquals("Account 123 not found", problemDetail.getDetail());
    }

    @Test
    void handleBusinessRuleViolation_Returns422ProblemDetail() {
        com.corebank.domain.exception.BusinessRuleViolationException ex = 
            new com.corebank.domain.exception.BusinessRuleViolationException("Insufficient funds");
            
        ProblemDetail problemDetail = exceptionHandler.handleBusinessRuleViolation(ex);
        
        assertNotNull(problemDetail);
        assertEquals(422, problemDetail.getStatus());
        assertEquals("Unprocessable Entity", problemDetail.getTitle());
        assertEquals("Insufficient funds", problemDetail.getDetail());
    }
}
