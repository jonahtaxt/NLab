package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.dto.PackageTypeDTO;
import com.effisoft.nlab.appointmentapi.entity.PackageType;
import com.effisoft.nlab.appointmentapi.exception.PackageTypeServiceException;
import com.effisoft.nlab.appointmentapi.repository.PackageTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class PackageTypeService {
    private final PackageTypeRepository packageTypeRepository;

    @Transactional
    public PackageType createPackageType(@Valid PackageTypeDTO dto) {
        try {
            PackageType packageType = new PackageType();
            updatePackageTypeFromDTO(packageType, dto);
            packageType.setActive(true);
            return packageTypeRepository.save(packageType);
        } catch (DataIntegrityViolationException e) {
            throw new PackageTypeServiceException("Failed to create package type due to data integrity violation", e);
        }
    }

    @Transactional(readOnly = true)
    public List<PackageType> getAllActivePackageTypes() {
        return packageTypeRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public PackageType getPackageTypeById(Integer id) {
        return packageTypeRepository.findById(id)
                .orElseThrow(() -> new PackageTypeServiceException("Package type not found with id: " + id));
    }

    @Transactional
    public PackageType updatePackageType(Integer id, @Valid PackageTypeDTO dto) {
        try {
            PackageType existingPackageType = getPackageTypeById(id);
            updatePackageTypeFromDTO(existingPackageType, dto);
            return packageTypeRepository.save(existingPackageType);
        } catch (DataIntegrityViolationException e) {
            throw new PackageTypeServiceException("Failed to update package type due to data integrity violation", e);
        }
    }

    @Transactional
    public void deactivatePackageType(Integer id) {
        PackageType packageType = getPackageTypeById(id);
        packageType.setActive(false);
        packageTypeRepository.save(packageType);
    }

    private void updatePackageTypeFromDTO(PackageType packageType, PackageTypeDTO dto) {
        packageType.setName(dto.getName().trim());
        packageType.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);
        packageType.setNumberOfAppointments(dto.getNumberOfAppointments());
        packageType.setBundle(dto.isBundle());
        packageType.setPrice(dto.getPrice());
        packageType.setNutritionistRate(dto.getNutritionistRate());
    }
}