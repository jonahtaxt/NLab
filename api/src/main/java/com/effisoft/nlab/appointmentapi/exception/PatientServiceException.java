package com.effisoft.nlab.appointmentapi.exception;

public class PatientServiceException extends AppointmentApiException {
    public PatientServiceException(String message) {
        super(message);
    }

    public PatientServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
