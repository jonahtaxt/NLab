package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.dto.PackageTypeDTO;
import com.effisoft.nlab.appointmentapi.entity.PackageType;
import com.effisoft.nlab.appointmentapi.exception.PackageTypeServiceException;
import com.effisoft.nlab.appointmentapi.repository.PackageTypeRepository;
import com.effisoft.nlab.appointmentapi.service.base.ServiceExceptionHandler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class PackageTypeService {
    private final PackageTypeRepository packageTypeRepository;

    @Transactional
    public PackageType createPackageType(@Valid PackageTypeDTO dto) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
            () -> {
                PackageType packageType = new PackageType();
                updatePackageTypeFromDTO(packageType, dto);
                packageType.setCreatedAt(LocalDateTime.now());
                packageType.setActive(true);
                return packageTypeRepository.save(packageType);
            }, 
            PackageTypeServiceException::new,
            "Create Package Type"
        );
    }

    @Transactional(readOnly = true)
    public List<PackageType> getAllActivePackageTypes() {
        return ServiceExceptionHandler.executeWithExceptionHandling(
            () -> packageTypeRepository.findByActiveTrue(),
            PackageTypeServiceException::new,
            "Get All Active Package Types"
        );
    }

    @Transactional(readOnly = true)
    public List<PackageType> getAllPackageTypes() {
        return ServiceExceptionHandler.executeWithExceptionHandling(
            packageTypeRepository::findAll,
            PackageTypeServiceException::new,
            "Get All Package Types"
        );
    }

    @Transactional(readOnly = true)
    public PackageType getPackageTypeById(Integer id) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
            () -> packageTypeRepository.findById(id)
                    .orElseThrow(() -> new PackageTypeServiceException("Package type not found with id: " + id)),
            PackageTypeServiceException::new,
            "Get Package Type By Id"
        );
    }

    @Transactional
    public PackageType updatePackageType(Integer id, @Valid PackageTypeDTO dto) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
            () -> {
                PackageType existingPackageType = getPackageTypeById(id);
                existingPackageType.setUpdatedAt(LocalDateTime.now());
                updatePackageTypeFromDTO(existingPackageType, dto);
                return packageTypeRepository.save(existingPackageType);
            },
            PackageTypeServiceException::new,
            "Update Package Type"
        );
    }

    @Transactional
    public PackageType deactivatePackageType(Integer id) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
            () -> {
                PackageType packageType = getPackageTypeById(id);
                packageType.setActive(false);
                return packageTypeRepository.save(packageType);
            },
            PackageTypeServiceException::new,
            "Deactivate Package Type"
        );
    }

    private void updatePackageTypeFromDTO(PackageType packageType, PackageTypeDTO dto) {
        packageType.setName(dto.getName().trim());
        packageType.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);
        packageType.setNumberOfAppointments(dto.getNumberOfAppointments());
        packageType.setBundle(dto.isBundle());
        packageType.setPrice(dto.getPrice());
        packageType.setNutritionistRate(dto.getNutritionistRate());
        packageType.setActive(dto.isActive());
    }
}