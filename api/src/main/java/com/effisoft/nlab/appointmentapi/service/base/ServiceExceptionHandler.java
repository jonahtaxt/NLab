package com.effisoft.nlab.appointmentapi.service.base;

import com.effisoft.nlab.appointmentapi.exception.AppointmentApiException;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.function.Supplier;

/**
 * Utility class for centralized exception handling in services
 */
public class ServiceExceptionHandler {

    /**
     * Execute a service operation with standardized exception handling
     * @param operation The operation to execute
     * @param exceptionType The type of exception to throw
     * @param errorMessagePrefix Prefix for error messages
     * @param <T> Return type of the operation
     * @param <E> Type of the exception to throw
     * @return The result of the operation
     * @throws E If an exception occurs
     */
    public static <T, E extends AppointmentApiException> T executeWithExceptionHandling(
            Supplier<T> operation,
            ExceptionFactory<E> exceptionFactory,
            String errorMessagePrefix) throws E {
        
        try {
            return operation.get();
        } catch (DataIntegrityViolationException e) {
            throw exceptionFactory.create(
                    errorMessagePrefix + " failed due to data integrity violation", e);
        } catch (AppointmentApiException e) {
            // Re-throw domain exceptions as-is
            throw exceptionFactory.create(e.getMessage(), e);
        } catch (Exception e) {
            throw exceptionFactory.create(
                    errorMessagePrefix + " failed due to unexpected error", e);
        }
    }

    /**
     * Functional interface for creating exceptions
     */
    @FunctionalInterface
    public interface ExceptionFactory<E extends AppointmentApiException> {
        E create(String message, Throwable cause);
    }
}