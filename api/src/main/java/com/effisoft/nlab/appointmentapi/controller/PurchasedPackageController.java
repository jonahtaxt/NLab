package com.effisoft.nlab.appointmentapi.controller;

import com.effisoft.nlab.appointmentapi.dto.PageResponseDTO;
import com.effisoft.nlab.appointmentapi.dto.PatientPurchasedPackageDTO;
import com.effisoft.nlab.appointmentapi.dto.PurchasedPackageDTO;
import com.effisoft.nlab.appointmentapi.entity.PurchasedPackage;
import com.effisoft.nlab.appointmentapi.service.PurchasedPackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<PatientPurchasedPackageDTO> getPurchasedPackageById(@PathVariable Integer id) {
        PatientPurchasedPackageDTO patientPurchasedPackage = purchasedPackageService
                .getPatientPurchasedPackageByPackageId(id);
        return ResponseEntity.ok(patientPurchasedPackage);
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

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST', 'PATIENT')")
    public ResponseEntity<PageResponseDTO<PurchasedPackage>> getPurchasedPackagesByPatient(
            @PathVariable Integer patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "purchaseDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<PurchasedPackage> packagesPage = purchasedPackageService.getPurchasedPackagesByPatientId(patientId,
                pageable);

        PageResponseDTO<PurchasedPackage> response = new PageResponseDTO<>(
                packagesPage.getContent(),
                packagesPage.getNumber(),
                packagesPage.getSize(),
                packagesPage.getTotalElements(),
                packagesPage.getTotalPages(),
                packagesPage.isFirst(),
                packagesPage.isLast());

        return ResponseEntity.ok(response);
    }
}