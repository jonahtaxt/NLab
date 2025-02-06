package com.effisoft.nlab.appointmentapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "CardPaymentType")
public class CardPaymentType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal bankFeePercentage;

    @Column(nullable = false)
    private Integer numberOfInstallments;

    @Column(nullable = false)
    private boolean isActive;
}
