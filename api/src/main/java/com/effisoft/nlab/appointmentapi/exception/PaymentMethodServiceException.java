package com.effisoft.nlab.appointmentapi.exception;

public class PaymentMethodServiceException extends AppointmentApiException {
    public PaymentMethodServiceException(String message) {
        super(message);
    }

    public PaymentMethodServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
