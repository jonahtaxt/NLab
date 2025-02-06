package com.effisoft.nlab.appointmentapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "PurchasedPackage")
public class PurchasedPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "PatientID", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "PackageTypeID", nullable = false)
    private PackageType packageType;

    @ManyToOne
    @JoinColumn(name = "PaymentMethodID", nullable = false)
    private PaymentMethod paymentMethod;

    @ManyToOne
    @JoinColumn(name = "CardPaymentTypeID")
    private CardPaymentType cardPaymentType;

    @Column(nullable = false)
    private LocalDateTime purchaseDate;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private Integer remainingAppointments;

    private LocalDateTime expirationDate;
}
