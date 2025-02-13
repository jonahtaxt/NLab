package com.effisoft.nlab.appointmentapi.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CardPaymentTypeDTO {
    private Integer id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;

    @NotNull(message = "Bank fee percentage is required")
    @DecimalMin(value = "0.0", message = "Bank fee percentage must be positive")
    @DecimalMax(value = "100.0", message = "Bank fee percentage must not exceed 100")
    @Digits(integer = 3, fraction = 2, message = "Bank fee percentage must have at most 3 digits and 2 decimal places")
    private BigDecimal bankFeePercentage;

    @NotNull(message = "Number of installments is required")
    @Min(value = 1, message = "Number of installments must be at least 1")
    @Max(value = 48, message = "Number of installments must not exceed 48")
    private Integer numberOfInstallments;

    private boolean isActive = true;
}