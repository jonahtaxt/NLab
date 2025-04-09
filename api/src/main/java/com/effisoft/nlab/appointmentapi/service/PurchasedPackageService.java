package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.dto.PatientPackagePaymentsDTO;
import com.effisoft.nlab.appointmentapi.dto.PatientPurchasedPackageDTO;
import com.effisoft.nlab.appointmentapi.dto.PurchasedPackageDTO;
import com.effisoft.nlab.appointmentapi.entity.*;
import com.effisoft.nlab.appointmentapi.exception.PurchasedPackageServiceException;
import com.effisoft.nlab.appointmentapi.repository.*;
import com.effisoft.nlab.appointmentapi.service.base.ServiceExceptionHandler;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class PurchasedPackageService {
        private final PurchasedPackageRepository purchasedPackageRepository;
        private final PatientRepository patientRepository;
        private final PackageTypeRepository packageTypeRepository;
        private final PatientPaymentRepository patientPaymentRepository;

        @Transactional
        public PurchasedPackage createPurchasedPackage(@Valid PurchasedPackageDTO dto) {
                return ServiceExceptionHandler.executeWithExceptionHandling(
                                () -> {
                                        PurchasedPackage purchasedPackage = new PurchasedPackage();

                                        // Set related entities
                                        Patient patient = patientRepository.findById(dto.getPatientId())
                                                        .orElseThrow(() -> new PurchasedPackageServiceException(
                                                                        "Patient not found"));

                                        PackageType packageType = packageTypeRepository.findById(dto.getPackageTypeId())
                                                        .orElseThrow(() -> new PurchasedPackageServiceException(
                                                                        "Package type not found"));

                                        purchasedPackage.setPatient(patient);
                                        purchasedPackage.setPackageType(packageType);

                                        // Set other fields
                                        purchasedPackage.setPurchaseDate(LocalDateTime.now());
                                        purchasedPackage.setPaidInFull(false);
                                        purchasedPackage.setRemainingAppointments(
                                                        packageType.getNumberOfAppointments());
                                        purchasedPackage.setExpirationDate(LocalDateTime.now().plusMonths(6));

                                        return purchasedPackageRepository.save(purchasedPackage);
                                }, PurchasedPackageServiceException::new, "Create Purchased Package");
        }

        @Transactional(readOnly = true)
        public List<PurchasedPackage> getAllPurchasedPackages() {
                return ServiceExceptionHandler.executeWithExceptionHandling(
                                purchasedPackageRepository::findAll,
                                PurchasedPackageServiceException::new,
                                "Get All Purchased Packages");
        }

        @Transactional(readOnly = true)
        public PurchasedPackage getPurchasedPackageById(Integer id) {
                return ServiceExceptionHandler.executeWithExceptionHandling(
                                () -> {
                                        return purchasedPackageRepository.findById(id)
                                                        .orElseThrow(() -> new PurchasedPackageServiceException(
                                                                        "Purchased package not found with id: " + id));
                                },
                                PurchasedPackageServiceException::new,
                                "Get Purchased Package by ID");
        }

        @Transactional
        public PurchasedPackage updatePurchasedPackage(Integer id, @Valid PurchasedPackageDTO dto) {
                return ServiceExceptionHandler.executeWithExceptionHandling(
                                () -> {
                                        PurchasedPackage existingPackage = getPurchasedPackageById(id);

                                        // Only allow updating certain fields
                                        existingPackage.setRemainingAppointments(dto.getRemainingAppointments());
                                        existingPackage.setExpirationDate(dto.getExpirationDate());
                                        existingPackage.setPaidInFull(dto.getPaidInFull());

                                        return purchasedPackageRepository.save(existingPackage);
                                },
                                PurchasedPackageServiceException::new,
                                "Update Purchased Package");
        }

        @Transactional(readOnly = true)
        public boolean isPackageValid(Integer id) {
                return ServiceExceptionHandler.executeWithExceptionHandling(
                                () -> {
                                        PurchasedPackage purchasedPackage = getPurchasedPackageById(id);
                                        LocalDateTime now = LocalDateTime.now();
                                        return purchasedPackage.getRemainingAppointments() > 0 &&
                                                        purchasedPackage.getExpirationDate().isAfter(now);
                                },
                                PurchasedPackageServiceException::new,
                                "Check if Package is Valid");
        }

        @Transactional(readOnly = true)
        public Page<PurchasedPackage> getPurchasedPackagesByPatientId(Integer patientId, Pageable pageable) {
                return ServiceExceptionHandler.executeWithExceptionHandling(
                                () -> {
                                        // Verify patient exists
                                        if (!patientRepository.existsById(patientId)) {
                                                throw new PurchasedPackageServiceException(
                                                                "Patient not found with id: " + patientId);
                                        }
                                        return purchasedPackageRepository.findByPatientId(patientId, pageable);
                                },
                                PurchasedPackageServiceException::new,
                                "Get Purchased Packages by Patient ID");
        }

        @Transactional(readOnly = true)
        public PatientPurchasedPackageDTO getPatientPurchasedPackageByPackageId(Integer purchasedPackageId) {
                return ServiceExceptionHandler.executeWithExceptionHandling(
                                () -> {
                                        PurchasedPackage purchasedPackage = getPurchasedPackageById(purchasedPackageId);
                                        PatientPurchasedPackageDTO patientPurchasedPackage = new PatientPurchasedPackageDTO();
                                        patientPurchasedPackage.setPurchasedPackage(purchasedPackage);
                                        List<PatientPayment> patientPayments = patientPaymentRepository
                                                        .findByPurchasedPackageId(purchasedPackageId);
                                        if (!patientPayments.isEmpty()) {
                                                patientPurchasedPackage.setPatientPayments(patientPayments.stream()
                                                                .map(payment -> {
                                                                        PatientPackagePaymentsDTO paymentDTO = new PatientPackagePaymentsDTO();
                                                                        paymentDTO.setPaymentDate(
                                                                                        payment.getPaymentDate());
                                                                        paymentDTO.setTotalPaid(payment.getTotalPaid());
                                                                        paymentDTO.setPaymentMethodName(
                                                                                        payment.getPaymentMethod()
                                                                                                        .getName());
                                                                        paymentDTO.setCardPaymentTypeName(
                                                                                        payment.getCardPaymentType() != null
                                                                                                        ? payment.getCardPaymentType()
                                                                                                                        .getName()
                                                                                                        : null);
                                                                        paymentDTO.setPurchasedPackageId(
                                                                                        payment.getPurchasedPackage()
                                                                                                        .getId());
                                                                        paymentDTO.setId(payment.getId());
                                                                        paymentDTO.setTotalPaid(payment.getTotalPaid());
                                                                        return paymentDTO;
                                                                })
                                                                .toList());
                                        }

                                        return patientPurchasedPackage;
                                },
                                PurchasedPackageServiceException::new,
                                "Get Patient Purchased Package by Package ID");
        }
}