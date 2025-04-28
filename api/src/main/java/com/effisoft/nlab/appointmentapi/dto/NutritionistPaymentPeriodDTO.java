package com.effisoft.nlab.appointmentapi.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class NutritionistPaymentPeriodDTO {
    private Integer id;

    @NotNull(message = "Nutritionist ID is required")
    private Integer nutritionistId;

    @NotNull(message = "Period start date is required")
    private LocalDate periodStartDate;

    @NotNull(message = "Period end date is required")
    private LocalDate periodEndDate;

    @NotNull(message = "Total appointments is required")
    @Min(value = 0, message = "Total appointments cannot be negative")
    private Integer totalAppointments;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Total amount must have at most 8 digits and 2 decimal places")
    private BigDecimal totalAmount;

    @NotBlank(message = "Payment status is required")
    @Pattern(regexp = "^(PENDING|PAID|CANCELADA)$", message = "Invalid payment status")
    private String paymentStatus;

    private LocalDateTime processedDate;
}