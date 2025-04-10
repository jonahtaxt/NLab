package com.effisoft.nlab.appointmentapi.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AppointmentDTO {
    private Integer id;

    @NotNull(message = "Purchased package ID is required")
    private Integer purchasedPackageId;

    @NotNull(message = "Nutritionist ID is required")
    private Integer nutritionistId;

    @NotNull(message = "Appointment date/time is required")
    @Future(message = "Appointment date/time must be in the future")
    private LocalDateTime appointmentDateTime;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(SCHEDULED|COMPLETED|CANCELLED|RESCHEDULED|NO_SHOW)$", 
            message = "Invalid status. Must be one of: SCHEDULED, COMPLETED, CANCELLED, RESCHEDULED, NO_SHOW")
    private String status;

    private String notes;
}