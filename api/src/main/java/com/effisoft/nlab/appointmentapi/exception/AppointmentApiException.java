package com.effisoft.nlab.appointmentapi.exception;

public abstract class AppointmentApiException extends RuntimeException {
    public AppointmentApiException(String message) {
        super(message);
    }

    public AppointmentApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
