package com.effisoft.nlab.appointmentapi.exception;

public class AppointmentServiceException extends AppointmentApiException {
    public AppointmentServiceException(String message) {
        super(message);
    }

    public AppointmentServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}