package com.effisoft.nlab.appointmentapi.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PurchasedPackageDTO {
    private Integer id;

    @NotNull(message = "Patient ID is required")
    private Integer patientId;

    @NotNull(message = "Package type ID is required")
    private Integer packageTypeId;

    @NotNull(message = "Payment method ID is required")
    private Integer paymentMethodId;

    private Integer cardPaymentTypeId;

    private LocalDateTime purchaseDate;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Total amount must have at most 8 digits and 2 decimal places")
    private BigDecimal totalAmount;

    @Min(value = 0, message = "Remaining appointments cannot be negative")
    private Integer remainingAppointments;

    private LocalDateTime expirationDate;
}