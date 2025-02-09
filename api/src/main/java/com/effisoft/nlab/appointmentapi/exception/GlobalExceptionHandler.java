package com.effisoft.nlab.appointmentapi.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

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
