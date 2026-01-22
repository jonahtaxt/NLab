package com.effisoft.nlab.appointmentapi.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "PatientPayment")
public class PatientPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Purchased Package is required")
    @ManyToOne
    @JoinColumn(name = "PurchasedPackageID", nullable = false)
    private PurchasedPackage purchasedPackage;

    @NotNull(message = "Payment Method is required")
    @ManyToOne
    @JoinColumn(name = "PaymentMethodID", nullable = false)
    private PaymentMethod paymentMethod;

    @ManyToOne
    @JoinColumn(name = "CardPaymentTypeID", nullable = false)
    private CardPaymentType cardPaymentType;

    @NotNull(message = "Total Paid is required")
    @Min(value = 1, message = "Total paid must be at least 1")
    @Column(nullable = false)
    private BigDecimal totalPaid;

    private LocalDateTime paymentDate;
}
