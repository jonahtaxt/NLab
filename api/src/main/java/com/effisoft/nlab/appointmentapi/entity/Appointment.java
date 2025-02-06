package com.effisoft.nlab.appointmentapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Appointment")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "PurchasedPackageID", nullable = false)
    private PurchasedPackage purchasedPackage;

    @ManyToOne
    @JoinColumn(name = "NutritionistID", nullable = false)
    private Nutritionist nutritionist;

    @Column(nullable = false)
    private LocalDateTime appointmentDateTime;

    @Column(nullable = false)
    private String status;

    private String notes;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
