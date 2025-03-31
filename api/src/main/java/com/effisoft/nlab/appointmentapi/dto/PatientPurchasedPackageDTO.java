package com.effisoft.nlab.appointmentapi.dto;

import java.util.List;

import com.effisoft.nlab.appointmentapi.entity.PurchasedPackage;

public class PatientPurchasedPackageDTO {
    private PurchasedPackage purchasedPackage;
    private List<PatientPackagePaymentsDTO> patientPayments;

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
        this.patientPayments = patientPayments;
    }
}
