package com.effisoft.nlab.appointmentapi.dto;

import java.math.BigDecimal;
import java.util.List;

import com.effisoft.nlab.appointmentapi.entity.PurchasedPackage;

public class PatientPurchasedPackageDTO {
    private PurchasedPackage purchasedPackage;
    private List<PatientPackagePaymentsDTO> patientPayments;
    private BigDecimal packagePaidTotal;

    public PurchasedPackage getPurchasedPackage() {
        return purchasedPackage;
    }

    public void setPurchasedPackage(PurchasedPackage purchasedPackage) {
        this.purchasedPackage = purchasedPackage;
    }

    public List<PatientPackagePaymentsDTO> getPatientPayments() {
        return patientPayments;
    }

    public void setPatientPayments(List<PatientPackagePaymentsDTO> patientPayments) {
        this.packagePaidTotal = BigDecimal.ZERO;
        this.patientPayments = patientPayments;

        if (this.patientPayments != null && !this.patientPayments.isEmpty()) {
            this.packagePaidTotal = this.patientPayments.stream()
                    .map(PatientPackagePaymentsDTO::getTotalPaid)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    public BigDecimal getPackagePaidTotal() {
        return packagePaidTotal;
    }
}
