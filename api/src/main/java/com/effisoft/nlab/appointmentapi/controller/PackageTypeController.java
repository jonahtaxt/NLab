package com.effisoft.nlab.appointmentapi.controller;

import com.effisoft.nlab.appointmentapi.dto.PackageTypeDTO;
import com.effisoft.nlab.appointmentapi.dto.PageResponseDTO;
import com.effisoft.nlab.appointmentapi.entity.PackageType;
import com.effisoft.nlab.appointmentapi.service.PackageTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponseDTO<PackageTypeDTO>> getAllPatients(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) Boolean active) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<PackageTypeDTO> packageTypes = packageTypeService.getPackageTypes(pageable, searchTerm, active);

        PageResponseDTO<PackageTypeDTO> response = new PageResponseDTO<>(
                packageTypes.getContent(),
                packageTypes.getNumber(),
                packageTypes.getSize(),
                packageTypes.getTotalElements(),
                packageTypes.getTotalPages(),
                packageTypes.isFirst(),
                packageTypes.isLast());

        return ResponseEntity.ok(response);
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