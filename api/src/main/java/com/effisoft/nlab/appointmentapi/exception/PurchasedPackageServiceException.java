package com.effisoft.nlab.appointmentapi.exception;

public class PurchasedPackageServiceException extends AppointmentApiException {
    public PurchasedPackageServiceException(String message) {
        super(message);
    }

    public PurchasedPackageServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
