package com.effisoft.nlab.appointmentapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.effisoft.nlab.appointmentapi.entity.PatientPayment;

public interface PatientPaymentRepository extends JpaRepository<PatientPayment, Integer> {
    List<PatientPayment> findByPurchasedPackageId(Integer purchasedPackageId);
}
