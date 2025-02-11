package com.effisoft.nlab.appointmentapi.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PackageTypeDTO {
    private Integer id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;

    @NotNull(message = "Number of appointments is required")
    @Min(value = 1, message = "Number of appointments must be at least 1")
    private Integer numberOfAppointments;

    private boolean isBundle;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price must have at most 8 digits and 2 decimal places")
    private BigDecimal price;

    @NotNull(message = "Nutritionist rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Nutritionist rate must be greater than 0")
    @DecimalMax(value = "1.0", message = "Nutritionist rate must not exceed 1.0")
    @Digits(integer = 1, fraction = 2, message = "Nutritionist rate must have at most 1 digit and 2 decimal places")
    private BigDecimal nutritionistRate;

    private boolean isActive;
}