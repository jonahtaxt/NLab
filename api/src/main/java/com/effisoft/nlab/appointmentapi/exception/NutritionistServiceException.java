package com.effisoft.nlab.appointmentapi.exception;

public class NutritionistServiceException extends AppointmentApiException{
    public NutritionistServiceException(String message) {
        super(message);
    }

    public NutritionistServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public NutritionistServiceException(String message, String errorCode) {
        super(message);
    }
}
