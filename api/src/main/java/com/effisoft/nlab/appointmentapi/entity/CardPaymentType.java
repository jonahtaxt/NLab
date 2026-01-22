package com.effisoft.nlab.appointmentapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Entity representing a card payment type with specific installment plan and bank fee configuration.
 * This entity is used to define different payment plans that can be offered to patients
 * when they purchase packages using credit or debit cards.
 */
@Data
@Entity
@Table(name = "CardPaymentType")
public class CardPaymentType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ0-9\\s-]*$",
            message = "Name must contain only letters, numbers, spaces, and hyphens")
    @Column(nullable = false, length = 50)
    private String name;

    @Size(max = 200, message = "Description must not exceed 200 characters")
    @Column(length = 200)
    private String description;

    /**
     * The percentage fee charged by the bank for this payment plan.
     * Example: 2.5 represents a 2.5% fee
     */
    @NotNull(message = "Bank fee percentage is required")
    @DecimalMin(value = "0.0", message = "Bank fee percentage must be positive")
    @DecimalMax(value = "100.0", message = "Bank fee percentage must not exceed 100")
    @Digits(integer = 3, fraction = 2,
            message = "Bank fee percentage must have at most 3 digits and 2 decimal places")
    @Column(name = "BankFeePercentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal bankFeePercentage;

    /**
     * Number of installments for this payment plan.
     * Minimum is 1 (immediate full payment)
     * Maximum is typically 48 (4 years) but can be configured
     */
    @NotNull(message = "Number of installments is required")
    @Min(value = 1, message = "Number of installments must be at least 1")
    @Max(value = 48, message = "Number of installments must not exceed 48")
    @Column(name = "NumberOfInstallments", nullable = false)
    private Integer numberOfInstallments;

    /**
     * Indicates whether this payment type is currently available for use
     */
    @Column(name = "Active", nullable = false)
    private boolean active = true;

    /**
     * Calculates the total fee amount for a given purchase amount
     * @param purchaseAmount the base amount before fees
     * @return the fee amount based on the bank fee percentage
     */
    public BigDecimal calculateFeeAmount(BigDecimal purchaseAmount) {
        return purchaseAmount.multiply(bankFeePercentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the installment amount for a given purchase amount
     * @param purchaseAmount the total amount including fees
     * @return the amount per installment
     */
    public BigDecimal calculateInstallmentAmount(BigDecimal purchaseAmount) {
        BigDecimal totalAmount = purchaseAmount.add(calculateFeeAmount(purchaseAmount));
        return totalAmount.divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.HALF_UP);
    }
}