package com.effisoft.nlab.appointmentapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "PackageType")
public class PackageType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Column(nullable = false, length = 50)
    private String name;

    @Size(max = 200, message = "Description must not exceed 200 characters")
    @Column(length = 200)
    private String description;

    @NotNull(message = "Number of appointments is required")
    @Min(value = 1, message = "Number of appointments must be at least 1")
    @Column(nullable = false)
    private Integer numberOfAppointments;

    @Column(nullable = false)
    private boolean isBundle;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price must have at most 8 digits and 2 decimal places")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Nutritionist rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Nutritionist rate must be greater than 0")
    @DecimalMax(value = "1.0", message = "Nutritionist rate must not exceed 1.0")
    @Digits(integer = 1, fraction = 2, message = "Nutritionist rate must have at most 1 digit and 2 decimal places")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal nutritionistRate;

    @Column(name = "Active", nullable = false)
    private boolean active;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime updatedAt;
}