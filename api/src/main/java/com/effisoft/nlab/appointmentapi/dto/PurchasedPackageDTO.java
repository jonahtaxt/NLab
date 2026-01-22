package com.effisoft.nlab.appointmentapi.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PurchasedPackageDTO {
    private Integer id;

    @NotNull(message = "Patient ID is required")
    private Integer patientId;

    @NotNull(message = "Package type ID is required")
    private Integer packageTypeId;

    private LocalDateTime purchaseDate;

    @Min(value = 0, message = "Remaining appointments cannot be negative")
    private Integer remainingAppointments;

    private LocalDateTime expirationDate;

    private Boolean paidInFull;
}