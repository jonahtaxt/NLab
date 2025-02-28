package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.dto.PackageTypeDTO;
import com.effisoft.nlab.appointmentapi.entity.PackageType;
import com.effisoft.nlab.appointmentapi.exception.PackageTypeServiceException;
import com.effisoft.nlab.appointmentapi.repository.PackageTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PackageTypeServiceTest {

    @Mock
    private PackageTypeRepository packageTypeRepository;

    @InjectMocks
    private PackageTypeService packageTypeService;

    private PackageTypeDTO validPackageTypeDTO;
    private PackageType existingPackageType;

    @BeforeEach
    void setUp() {
        // Set up valid DTO
        validPackageTypeDTO = new PackageTypeDTO();
        validPackageTypeDTO.setName("Basic Package");
        validPackageTypeDTO.setDescription("Basic nutrition package");
        validPackageTypeDTO.setNumberOfAppointments(4);
        validPackageTypeDTO.setBundle(false);
        validPackageTypeDTO.setPrice(new BigDecimal("100.00"));
        validPackageTypeDTO.setNutritionistRate(new BigDecimal("0.70"));
        validPackageTypeDTO.setActive(true);

        // Set up existing package type
        existingPackageType = new PackageType();
        existingPackageType.setId(1);
        existingPackageType.setName("Basic Package");
        existingPackageType.setDescription("Basic nutrition package");
        existingPackageType.setNumberOfAppointments(4);
        existingPackageType.setBundle(false);
        existingPackageType.setPrice(new BigDecimal("100.00"));
        existingPackageType.setNutritionistRate(new BigDecimal("0.70"));
        existingPackageType.setActive(true);
    }

    @Test
    void createPackageType_WhenValidDTO_ShouldCreatePackageType() {
        // Arrange
        when(packageTypeRepository.save(any(PackageType.class))).thenReturn(existingPackageType);

        // Act
        PackageType created = packageTypeService.createPackageType(validPackageTypeDTO);

        // Assert
        assertNotNull(created);
        assertEquals(validPackageTypeDTO.getName(), created.getName());
        assertTrue(created.isActive());
        verify(packageTypeRepository).save(any(PackageType.class));
    }

    @Test
    void createPackageType_WhenDataIntegrityViolation_ShouldThrowException() {
        // Arrange
        when(packageTypeRepository.save(any(PackageType.class)))
                .thenThrow(new DataIntegrityViolationException("Database error"));

        // Act & Assert
        PackageTypeServiceException exception = assertThrows(
                PackageTypeServiceException.class,
                () -> packageTypeService.createPackageType(validPackageTypeDTO)
        );

        assertEquals(
                "Failed to create package type due to data integrity violation",
                exception.getMessage()
        );
    }

    @Test
    void getAllActivePackageTypes_ShouldReturnOnlyActivePackageTypes() {
        // Arrange
        List<PackageType> activePackageTypes = Arrays.asList(existingPackageType);
        when(packageTypeRepository.findByActiveTrue()).thenReturn(activePackageTypes);

        // Act
        List<PackageType> result = packageTypeService.getAllActivePackageTypes();

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
        verify(packageTypeRepository).findByActiveTrue();
    }

    @Test
    void getPackageTypeById_WhenExists_ShouldReturnPackageType() {
        // Arrange
        when(packageTypeRepository.findById(1)).thenReturn(Optional.of(existingPackageType));

        // Act
        PackageType result = packageTypeService.getPackageTypeById(1);

        // Assert
        assertNotNull(result);
        assertEquals(existingPackageType.getId(), result.getId());
        verify(packageTypeRepository).findById(1);
    }

    @Test
    void getPackageTypeById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(packageTypeRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        PackageTypeServiceException exception = assertThrows(
                PackageTypeServiceException.class,
                () -> packageTypeService.getPackageTypeById(999)
        );

        assertEquals("Package type not found with id: 999", exception.getMessage());
        verify(packageTypeRepository).findById(999);
    }

    @Test
    void deactivatePackageType_WhenExists_ShouldDeactivatePackageType() {
        // Arrange
        when(packageTypeRepository.findById(1)).thenReturn(Optional.of(existingPackageType));
        when(packageTypeRepository.save(any(PackageType.class))).thenReturn(existingPackageType);

        // Act
        packageTypeService.deactivatePackageType(1);

        // Assert
        assertFalse(existingPackageType.isActive());
        verify(packageTypeRepository).findById(1);
        verify(packageTypeRepository).save(existingPackageType);
    }
}