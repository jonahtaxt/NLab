package com.effisoft.nlab.appointmentapi.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PatientPaymentDTO {
    private Integer id;

    @NotNull(message = "Purchased Package ID is required")
    private Integer purchasedPackageId;

    @NotNull(message = "Payment Method ID is required")
    private Integer paymentMethodId;

    private Integer cardPaymentTypeId;

    @Min(value = 1, message = "Total Paid must be at least 1")
    private BigDecimal totalPaid;
}
