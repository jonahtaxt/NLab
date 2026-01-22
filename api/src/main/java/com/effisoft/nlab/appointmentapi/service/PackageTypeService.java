package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.dto.PackageTypeDTO;
import com.effisoft.nlab.appointmentapi.dto.PackageTypeSelectDTO;
import com.effisoft.nlab.appointmentapi.entity.PackageType;
import com.effisoft.nlab.appointmentapi.exception.PackageTypeServiceException;
import com.effisoft.nlab.appointmentapi.mapper.PackageTypeMapper;
import com.effisoft.nlab.appointmentapi.repository.PackageTypeRepository;
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
public class PackageTypeService {
    private final PackageTypeRepository packageTypeRepository;
    private final PackageTypeMapper packageTypeMapper;

    @Transactional
    public PackageType createPackageType(@Valid PackageTypeDTO dto) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
                () -> {
                    PackageType packageType = packageTypeMapper.toEntity(dto);
                    packageType.setCreatedAt(LocalDateTime.now());
                    packageType.setActive(true);
                    return packageTypeRepository.save(packageType);
                },
                PackageTypeServiceException::new,
                "Create Package Type");
    }

    @Transactional(readOnly = true)
    public List<PackageType> getAllActivePackageTypes() {
        return ServiceExceptionHandler.executeWithExceptionHandling(
                () -> packageTypeRepository.findByActiveTrue(),
                PackageTypeServiceException::new,
                "Get All Active Package Types");
    }

    @Transactional(readOnly = true)
    public List<PackageType> getAllPackageTypes() {
        return ServiceExceptionHandler.executeWithExceptionHandling(
                packageTypeRepository::findAll,
                PackageTypeServiceException::new,
                "Get All Package Types");
    }

    @Transactional(readOnly = true)
    public List<PackageTypeSelectDTO> getSelectActivePackageTypes() {
        return ServiceExceptionHandler.executeWithExceptionHandling(
                () -> {
                    return packageTypeMapper.selectToDtoList(getAllActivePackageTypes());
                },
                PackageTypeServiceException::new,
                "Get Select Active Package Types");
    }

    @Transactional(readOnly = true)
    public Page<PackageTypeDTO> getPackageTypes(Pageable pageable,
            String searchTerm,
            Boolean active) {
        return packageTypeRepository.findPackageTypes(searchTerm, active, pageable)
                .map(packageTypeMapper::toDto);
    }

    @Transactional(readOnly = true)
    public PackageType getPackageTypeById(Integer id) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
                () -> packageTypeRepository.findById(id)
                        .orElseThrow(() -> new PackageTypeServiceException("Package type not found with id: " + id)),
                PackageTypeServiceException::new,
                "Get Package Type By Id");
    }

    @Transactional
    public PackageType updatePackageType(Integer id, @Valid PackageTypeDTO dto) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
                () -> {
                    PackageType existingPackageType = getPackageTypeById(id);
                    packageTypeMapper.updatePackageTypeFromDTO(dto, existingPackageType);
                    existingPackageType.setUpdatedAt(LocalDateTime.now());
                    return packageTypeRepository.save(existingPackageType);
                },
                PackageTypeServiceException::new,
                "Update Package Type");
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
                "Deactivate Package Type");
    }
}