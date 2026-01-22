package com.effisoft.nlab.appointmentapi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PatientPackagePaymentsDTO {
    private Integer id;
    private Integer purchasedPackageId;
    private String paymentMethodName;
    private String cardPaymentTypeName;
    private LocalDateTime paymentDate;
    private BigDecimal totalPaid;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getPurchasedPackageId() {
        return purchasedPackageId;
    }
    public void setPurchasedPackageId(Integer purchasedPackageId) {
        this.purchasedPackageId = purchasedPackageId;
    }
    public String getPaymentMethodName() {
        return paymentMethodName;
    }
    public void setPaymentMethodName(String paymentMethodName) {
        this.paymentMethodName = paymentMethodName;
    }
    public String getCardPaymentTypeName() {
        return cardPaymentTypeName;
    }
    public void setCardPaymentTypeName(String cardPaymentTypeName) {
        this.cardPaymentTypeName = cardPaymentTypeName;
    }
    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }
    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
    public BigDecimal getTotalPaid() {
        return totalPaid;
    }
    public void setTotalPaid(BigDecimal totalPaid) {
        this.totalPaid = totalPaid;
    }
}
