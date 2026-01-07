package com.chencraft.ntu.exception;

import com.chencraft.ntu.model.response.GenericResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for the banking system.
 * Intercepts exceptions and returns a consistent GenericResponse with appropriate status codes.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handles custom OperationFailedException.
     *
     * @param e the caught OperationFailedException
     * @return a response containing the error details
     */
    @ExceptionHandler(OperationFailedException.class)
    public ResponseEntity<GenericResponse> handleOperationFailedException(OperationFailedException e) {
        log.error("Operation failed", e);

        GenericResponse response = new GenericResponse();
        response.setMessage(e.getErrorMessage());
        response.setStatusCode(e.getErrorCode());
        return ResponseEntity.ok(response);
    }

    /**
     * Handles any other unexpected exceptions.
     *
     * @param e the caught Exception
     * @return a response containing the exception message and a 500 status code
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponse> handleException(Exception e) {
        log.error("Unexpected error", e);

        GenericResponse response = new GenericResponse();
        response.setMessage(e.getMessage());
        response.setStatusCode(500);
        return ResponseEntity.ok(response);
    }
}
