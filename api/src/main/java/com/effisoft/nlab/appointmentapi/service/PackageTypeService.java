package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.entity.PackageType;
import com.effisoft.nlab.appointmentapi.exception.PackageTypeServiceException;
import com.effisoft.nlab.appointmentapi.repository.PackageTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PackageTypeService {
    private final PackageTypeRepository packageTypeRepository;

    @Transactional
    public PackageType createPackageType(PackageType packageType) {
        packageType.setActive(true);
        return packageTypeRepository.save(packageType);
    }

    @Transactional(readOnly = true)
    public List<PackageType> getAllActivePackageTypes() {
        return packageTypeRepository.findByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public Optional<PackageType> getPackageTypeById(Integer id) {
        return packageTypeRepository.findById(id);
    }

    @Transactional
    public PackageType updatePackageType(Integer id, PackageType updatedPackageType) {
        return packageTypeRepository.findById(id)
                .map(existingPackageType -> {
                    existingPackageType.setName(updatedPackageType.getName());
                    existingPackageType.setDescription(updatedPackageType.getDescription());
                    existingPackageType.setNumberOfAppointments(updatedPackageType.getNumberOfAppointments());
                    existingPackageType.setBundle(updatedPackageType.isBundle());
                    existingPackageType.setPrice(updatedPackageType.getPrice());
                    existingPackageType.setNutritionistRate(updatedPackageType.getNutritionistRate());
                    return packageTypeRepository.save(existingPackageType);
                })
                .orElseThrow(() -> new PackageTypeServiceException("Package Type not found"));
    }

    @Transactional
    public void deactivatePackageType(Integer id) {
        PackageType packageType = packageTypeRepository.findById(id)
                .orElseThrow(() -> new PackageTypeServiceException("Package Type not found"));

        packageType.setActive(false);
        packageTypeRepository.save(packageType);
    }
}
