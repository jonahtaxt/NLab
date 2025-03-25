package com.effisoft.nlab.appointmentapi.exception;

public class PatientPaymentException extends AppointmentApiException {
    public PatientPaymentException(String message) {
        super(message);
    }

    public PatientPaymentException(String message, Throwable cause) {
        super(message, cause);
    }

}
