package com.effisoft.nlab.appointmentapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "NutritionistPaymentPeriod")
public class NutritionistPaymentPeriod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Nutritionist is required")
    @ManyToOne
    @JoinColumn(name = "NutritionistID", nullable = false)
    private Nutritionist nutritionist;

    @NotNull(message = "Period start date is required")
    @Column(nullable = false)
    private LocalDate periodStartDate;

    @NotNull(message = "Period end date is required")
    @Column(nullable = false)
    private LocalDate periodEndDate;

    @NotNull(message = "Total appointments is required")
    @Min(value = 0, message = "Total appointments cannot be negative")
    @Column(nullable = false)
    private Integer totalAppointments;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Total amount must have at most 8 digits and 2 decimal places")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @NotBlank(message = "Payment status is required")
    @Pattern(regexp = "^(PENDING|PAID|CANCELADA)$", message = "Invalid payment status")
    @Column(nullable = false)
    private String paymentStatus;

    private LocalDateTime processedDate;
}