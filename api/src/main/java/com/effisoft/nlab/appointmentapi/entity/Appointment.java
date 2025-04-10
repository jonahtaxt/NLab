package com.effisoft.nlab.appointmentapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Appointment")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Purchased package is required")
    @ManyToOne
    @JoinColumn(name = "PurchasedPackageID", nullable = false)
    private PurchasedPackage purchasedPackage;

    @NotNull(message = "Nutritionist is required")
    @ManyToOne
    @JoinColumn(name = "NutritionistID", nullable = false)
    private Nutritionist nutritionist;

    @NotNull(message = "Appointment date/time is required")
    @Future(message = "Appointment date/time must be in the future")
    @Column(nullable = false)
    private LocalDateTime appointmentDateTime;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(SCHEDULED|COMPLETED|CANCELLED|RESCHEDULED|NO_SHOW)$", 
            message = "Invalid appointment status")
    @Column(nullable = false)
    private String status;

    private String notes;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}