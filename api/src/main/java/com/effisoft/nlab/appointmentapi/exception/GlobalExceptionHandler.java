package com.effisoft.nlab.appointmentapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NutritionistServiceException.class)
    public ResponseEntity<ErrorResponse> handleNutritionistServiceException (NutritionistServiceException ex,
                                                                             WebRequest request) {
        return new ResponseEntity<>(buildErrorResponse("Patient Service Error",
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getDescription(false)), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<ErrorResponse> handlePatientServiceException(PatientServiceException ex,
                                                                       WebRequest request) {
        return new ResponseEntity<>(buildErrorResponse("Patient Service Error",
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getDescription(false)), HttpStatus.INTERNAL_SERVER_ERROR);
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
