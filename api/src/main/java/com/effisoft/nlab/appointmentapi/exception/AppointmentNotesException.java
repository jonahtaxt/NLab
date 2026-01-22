package com.effisoft.nlab.appointmentapi.exception;

public class AppointmentNotesException extends AppointmentApiException {
    public AppointmentNotesException(String message) {
        super(message);
    }

    public AppointmentNotesException(String message, Throwable cause) {
        super(message, cause);
    }
}
