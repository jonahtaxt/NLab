package com.effisoft.nlab.appointmentapi.exception;

public class CardPaymentTypeServiceException extends AppointmentApiException {
    public CardPaymentTypeServiceException(String message) {
        super(message);
    }

    public CardPaymentTypeServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
