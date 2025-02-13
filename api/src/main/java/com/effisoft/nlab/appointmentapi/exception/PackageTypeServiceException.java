package com.effisoft.nlab.appointmentapi.exception;

public class PackageTypeServiceException extends AppointmentApiException {
    public PackageTypeServiceException (String message) {
        super(message);
    }

    public PackageTypeServiceException (String message, Throwable cause) {
        super(message, cause);
    }
}
