package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.dto.PurchasedPackageDTO;
import com.effisoft.nlab.appointmentapi.entity.*;
import com.effisoft.nlab.appointmentapi.exception.PurchasedPackageServiceException;
import com.effisoft.nlab.appointmentapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
    private final PaymentMethodRepository paymentMethodRepository;
    private final CardPaymentTypeRepository cardPaymentTypeRepository;

    @Transactional
    public PurchasedPackage createPurchasedPackage(@Valid PurchasedPackageDTO dto) {
        try {
            PurchasedPackage purchasedPackage = new PurchasedPackage();

            // Set related entities
            Patient patient = patientRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new PurchasedPackageServiceException("Patient not found"));

            PackageType packageType = packageTypeRepository.findById(dto.getPackageTypeId())
                    .orElseThrow(() -> new PurchasedPackageServiceException("Package type not found"));

            PaymentMethod paymentMethod = paymentMethodRepository.findById(dto.getPaymentMethodId())
                    .orElseThrow(() -> new PurchasedPackageServiceException("Payment method not found"));

            purchasedPackage.setPatient(patient);
            purchasedPackage.setPackageType(packageType);
            purchasedPackage.setPaymentMethod(paymentMethod);

            // Set card payment type if provided
            if (dto.getCardPaymentTypeId() != null) {
                CardPaymentType cardPaymentType = cardPaymentTypeRepository.findById(dto.getCardPaymentTypeId())
                        .orElseThrow(() -> new PurchasedPackageServiceException("Card payment type not found"));
                purchasedPackage.setCardPaymentType(cardPaymentType);
            }

            // Set other fields
            purchasedPackage.setPurchaseDate(LocalDateTime.now());
            purchasedPackage.setTotalAmount(dto.getTotalAmount());
            purchasedPackage.setRemainingAppointments(packageType.getNumberOfAppointments());
            purchasedPackage.setExpirationDate(LocalDateTime.now().plusMonths(6));

            return purchasedPackageRepository.save(purchasedPackage);
        } catch (DataIntegrityViolationException e) {
            throw new PurchasedPackageServiceException("Failed to create purchased package due to data integrity violation", e);
        }
    }

    @Transactional(readOnly = true)
    public List<PurchasedPackage> getAllPurchasedPackages() {
        return purchasedPackageRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PurchasedPackage getPurchasedPackageById(Integer id) {
        return purchasedPackageRepository.findById(id)
                .orElseThrow(() -> new PurchasedPackageServiceException("Purchased package not found with id: " + id));
    }

    @Transactional
    public PurchasedPackage updatePurchasedPackage(Integer id, @Valid PurchasedPackageDTO dto) {
        try {
            PurchasedPackage existingPackage = getPurchasedPackageById(id);

            // Only allow updating certain fields
            existingPackage.setRemainingAppointments(dto.getRemainingAppointments());
            existingPackage.setExpirationDate(dto.getExpirationDate());

            return purchasedPackageRepository.save(existingPackage);
        } catch (DataIntegrityViolationException e) {
            throw new PurchasedPackageServiceException("Failed to update purchased package due to data integrity violation", e);
        }
    }

    @Transactional(readOnly = true)
    public boolean isPackageValid(Integer id) {
        PurchasedPackage purchasedPackage = getPurchasedPackageById(id);
        LocalDateTime now = LocalDateTime.now();
        return purchasedPackage.getRemainingAppointments() > 0 &&
                purchasedPackage.getExpirationDate().isAfter(now);
    }
}