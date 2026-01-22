package com.effisoft.nlab.appointmentapi.repository;

import com.effisoft.nlab.appointmentapi.entity.PurchasedPackage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchasedPackageRepository extends JpaRepository<PurchasedPackage, Integer> {
    Page<PurchasedPackage> findByPatientId(Integer patientId, Pageable pageable);
}
