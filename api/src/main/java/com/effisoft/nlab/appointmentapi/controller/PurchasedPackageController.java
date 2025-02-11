package com.effisoft.nlab.appointmentapi.controller;

import com.effisoft.nlab.appointmentapi.dto.PurchasedPackageDTO;
import com.effisoft.nlab.appointmentapi.entity.PurchasedPackage;
import com.effisoft.nlab.appointmentapi.service.PurchasedPackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchased-packages")
@RequiredArgsConstructor
public class PurchasedPackageController {
    private final PurchasedPackageService purchasedPackageService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")
    public ResponseEntity<PurchasedPackage> createPurchasedPackage(
            @Valid @RequestBody PurchasedPackageDTO purchasedPackageDTO) {
        PurchasedPackage createdPackage = purchasedPackageService.createPurchasedPackage(purchasedPackageDTO);
        return new ResponseEntity<>(createdPackage, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")
    public ResponseEntity<List<PurchasedPackage>> getAllPurchasedPackages() {
        List<PurchasedPackage> packages = purchasedPackageService.getAllPurchasedPackages();
        return ResponseEntity.ok(packages);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST', 'PATIENT')")
    public ResponseEntity<PurchasedPackage> getPurchasedPackageById(@PathVariable Integer id) {
        PurchasedPackage purchasedPackage = purchasedPackageService.getPurchasedPackageById(id);
        return ResponseEntity.ok(purchasedPackage);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")
    public ResponseEntity<PurchasedPackage> updatePurchasedPackage(
            @PathVariable Integer id,
            @Valid @RequestBody PurchasedPackageDTO purchasedPackageDTO) {
        PurchasedPackage updatedPackage = purchasedPackageService.updatePurchasedPackage(id, purchasedPackageDTO);
        return ResponseEntity.ok(updatedPackage);
    }

    @GetMapping("/{id}/valid")
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST', 'PATIENT')")
    public ResponseEntity<Boolean> isPackageValid(@PathVariable Integer id) {
        boolean isValid = purchasedPackageService.isPackageValid(id);
        return ResponseEntity.ok(isValid);
    }
}