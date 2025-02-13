package com.effisoft.nlab.appointmentapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PaymentMethodDTO {
    private Integer id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s]*$", message = "Name must contain only letters and spaces")
    private String name;

    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;
}