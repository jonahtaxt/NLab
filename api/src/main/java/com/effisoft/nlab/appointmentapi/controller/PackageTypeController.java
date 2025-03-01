package com.effisoft.nlab.appointmentapi.controller;

import com.effisoft.nlab.appointmentapi.dto.PackageTypeDTO;
import com.effisoft.nlab.appointmentapi.entity.PackageType;
import com.effisoft.nlab.appointmentapi.service.PackageTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/package-types")
@RequiredArgsConstructor
public class PackageTypeController {
    private final PackageTypeService packageTypeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PackageType> createPackageType(@Valid @RequestBody PackageTypeDTO packageTypeDTO) {
        PackageType createdPackageType = packageTypeService.createPackageType(packageTypeDTO);
        return new ResponseEntity<>(createdPackageType, HttpStatus.CREATED);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")
    public ResponseEntity<List<PackageType>> getAllActivePackageTypes() {
        List<PackageType> activePackageTypes = packageTypeService.getAllActivePackageTypes();
        return ResponseEntity.ok(activePackageTypes);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PackageType>> getAllPackageTypes() {
        List<PackageType> packageTypes = packageTypeService.getAllPackageTypes();
        return ResponseEntity.ok(packageTypes);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")
    public ResponseEntity<PackageType> getPackageTypeById(@PathVariable Integer id) {
        PackageType packageType = packageTypeService.getPackageTypeById(id);
        return ResponseEntity.ok(packageType);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PackageType> updatePackageType(
            @PathVariable Integer id,
            @Valid @RequestBody PackageTypeDTO packageTypeDTO) {
        PackageType updatedPackageType = packageTypeService.updatePackageType(id, packageTypeDTO);
        return ResponseEntity.ok(updatedPackageType);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivatePackageType(@PathVariable Integer id) {
        packageTypeService.deactivatePackageType(id);
        return ResponseEntity.noContent().build();
    }
}