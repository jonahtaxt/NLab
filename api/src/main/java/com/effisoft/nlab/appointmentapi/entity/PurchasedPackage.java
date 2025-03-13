package com.effisoft.nlab.appointmentapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "PurchasedPackage")
public class PurchasedPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Patient is required")
    @ManyToOne
    @JoinColumn(name = "PatientID", nullable = false)
    private Patient patient;

    @NotNull(message = "Package type is required")
    @ManyToOne
    @JoinColumn(name = "PackageTypeID", nullable = false)
    private PackageType packageType;

    @NotNull(message = "Payment method is required")
    @ManyToOne
    @JoinColumn(name = "PaymentMethodID", nullable = false)
    private PaymentMethod paymentMethod;

    @ManyToOne
    @JoinColumn(name = "CardPaymentTypeID")
    private CardPaymentType cardPaymentType;

    @Column(nullable = false)
    private LocalDateTime purchaseDate;

    @NotNull(message = "Remaining appointments is required")
    @Min(value = 0, message = "Remaining appointments cannot be negative")
    @Column(nullable = false)
    private Integer remainingAppointments;

    private LocalDateTime expirationDate;
}