package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.entity.PurchasedPackage;
import com.effisoft.nlab.appointmentapi.exception.PurchasedPackageServiceException;
import com.effisoft.nlab.appointmentapi.repository.PurchasedPackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PurchasedPackageService {
    private final PurchasedPackageRepository purchasedPackageRepository;

    @Transactional
    public PurchasedPackage createPurchasedPackage(PurchasedPackage purchasedPackage) {
        // Set purchase date to current time if not provided
        if (purchasedPackage.getPurchaseDate() == null) {
            purchasedPackage.setPurchaseDate(LocalDateTime.now());
        }

        // Set initial remaining appointments from package type
        if (purchasedPackage.getRemainingAppointments() == null) {
            purchasedPackage.setRemainingAppointments(
                    purchasedPackage.getPackageType().getNumberOfAppointments()
            );
        }

        // Calculate expiration date if not provided (e.g., 6 months from purchase)
        if (purchasedPackage.getExpirationDate() == null) {
            purchasedPackage.setExpirationDate(
                    purchasedPackage.getPurchaseDate().plusMonths(6)
            );
        }

        return purchasedPackageRepository.save(purchasedPackage);
    }

    @Transactional(readOnly = true)
    public List<PurchasedPackage> getAllPurchasedPackages() {
        return purchasedPackageRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<PurchasedPackage> getPurchasedPackageById(Integer id) {
        return purchasedPackageRepository.findById(id);
    }

    @Transactional
    public PurchasedPackage updatePurchasedPackage(Integer id, PurchasedPackage updatedPackage) {
        return purchasedPackageRepository.findById(id)
                .map(existingPackage -> {
                    // Update only allowed fields
                    existingPackage.setRemainingAppointments(updatedPackage.getRemainingAppointments());
                    existingPackage.setExpirationDate(updatedPackage.getExpirationDate());
                    return purchasedPackageRepository.save(existingPackage);
                })
                .orElseThrow(() -> new PurchasedPackageServiceException("Purchased Package not found"));
    }

    @Transactional(readOnly = true)
    public boolean isPackageValid(Integer id) {
        return purchasedPackageRepository.findById(id)
                .map(pkg -> {
                    LocalDateTime now = LocalDateTime.now();
                    return pkg.getRemainingAppointments() > 0 &&
                            pkg.getExpirationDate().isAfter(now);
                })
                .orElse(false);
    }
}