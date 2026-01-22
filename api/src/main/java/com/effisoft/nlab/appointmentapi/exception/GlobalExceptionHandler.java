package com.effisoft.nlab.appointmentapi.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AppointmentApiException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(AppointmentApiException ex,
                                                                    WebRequest request) {
        // Log the error with trace ID
        log.error("Application error occurred: {}", ex.getMessage(), ex);

        return new ResponseEntity<>(buildErrorResponse(
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false)),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(Exception ex, WebRequest request) {
        // Log the authentication error
        log.error("Authentication error: {}", ex.getMessage());

        return new ResponseEntity<>(buildErrorResponse(
                "Authorization Error",
                "You don't have permission to access this resource",
                HttpStatus.FORBIDDEN.value(),
                request.getDescription(false)),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        // Log unexpected errors
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        return new ResponseEntity<>(buildErrorResponse(
                "Internal Server Error",
                "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getDescription(false)),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            ConstraintViolationException ex, WebRequest request) {

        String errorMessage = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                errorMessage,
                request.getDescription(false)
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, WebRequest request) {

        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                errorMessage,
                request.getDescription(false)
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private ErrorResponse buildErrorResponse(String error, String message, int httpStatus, String requestDescription) {
        return new ErrorResponse(
                LocalDateTime.now(),
                httpStatus,
                error,
                message,
                requestDescription
        );
    }
}