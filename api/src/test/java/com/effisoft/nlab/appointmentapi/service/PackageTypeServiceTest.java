package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.dto.PackageTypeDTO;
import com.effisoft.nlab.appointmentapi.entity.PackageType;
import com.effisoft.nlab.appointmentapi.exception.PackageTypeServiceException;
import com.effisoft.nlab.appointmentapi.mapper.PackageTypeMapper;
import com.effisoft.nlab.appointmentapi.repository.PackageTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PackageTypeServiceTest {

    @Mock
    private PackageTypeRepository packageTypeRepository;

    @Mock
    private PackageTypeMapper packageTypeMapper;

    @InjectMocks
    private PackageTypeService packageTypeService;

    private PackageTypeDTO validPackageTypeDTO;
    private PackageType existingPackageType;
    private PackageTypeDTO existingPackageTypeDTO;

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
        existingPackageType.setCreatedAt(LocalDateTime.now());

        // Set up DTO from existing entity (for mapper tests)
        existingPackageTypeDTO = new PackageTypeDTO();
        existingPackageTypeDTO.setId(1);
        existingPackageTypeDTO.setName("Basic Package");
        existingPackageTypeDTO.setDescription("Basic nutrition package");
        existingPackageTypeDTO.setNumberOfAppointments(4);
        existingPackageTypeDTO.setBundle(false);
        existingPackageTypeDTO.setPrice(new BigDecimal("100.00"));
        existingPackageTypeDTO.setNutritionistRate(new BigDecimal("0.70"));
        existingPackageTypeDTO.setActive(true);
    }

    @Test
    void createPackageType_WhenValidDTO_ShouldCreatePackageType() {
        // Arrange
        when(packageTypeMapper.toEntity(any(PackageTypeDTO.class))).thenReturn(existingPackageType);
        when(packageTypeRepository.save(any(PackageType.class))).thenReturn(existingPackageType);

        // Act
        PackageType created = packageTypeService.createPackageType(validPackageTypeDTO);

        // Assert
        assertNotNull(created);
        assertEquals(validPackageTypeDTO.getName(), created.getName());
        assertTrue(created.isActive());
        verify(packageTypeMapper).toEntity(validPackageTypeDTO);
        verify(packageTypeRepository).save(any(PackageType.class));
    }

    @Test
    void createPackageType_WhenDataIntegrityViolation_ShouldThrowException() {
        // Arrange
        when(packageTypeMapper.toEntity(any(PackageTypeDTO.class))).thenReturn(existingPackageType);
        when(packageTypeRepository.save(any(PackageType.class)))
                .thenThrow(new DataIntegrityViolationException("Database error"));

        // Act & Assert
        PackageTypeServiceException exception = assertThrows(
                PackageTypeServiceException.class,
                () -> packageTypeService.createPackageType(validPackageTypeDTO));

        assertEquals(
                "Create Package Type failed due to data integrity violation",
                exception.getMessage());
        verify(packageTypeMapper).toEntity(validPackageTypeDTO);
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
    void getAllPackageTypes_ShouldReturnAllPackageTypes() {
        // Arrange
        List<PackageType> allPackageTypes = Arrays.asList(existingPackageType);
        when(packageTypeRepository.findAll()).thenReturn(allPackageTypes);

        // Act
        List<PackageType> result = packageTypeService.getAllPackageTypes();

        // Assert
        assertEquals(1, result.size());
        verify(packageTypeRepository).findAll();
    }

    @Test
    void getPackageTypes_ShouldReturnPaginatedPackageTypes() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        String searchTerm = "Basic";
        Boolean active = true;
        
        Page<PackageType> packageTypePage = new PageImpl<>(Arrays.asList(existingPackageType));
        when(packageTypeRepository.findPackageTypes(searchTerm, active, pageable)).thenReturn(packageTypePage);
        when(packageTypeMapper.toDto(existingPackageType)).thenReturn(existingPackageTypeDTO);

        // Act
        Page<PackageTypeDTO> result = packageTypeService.getPackageTypes(pageable, searchTerm, active);

        // Assert
        assertEquals(1, result.getTotalElements());
        verify(packageTypeRepository).findPackageTypes(searchTerm, active, pageable);
        verify(packageTypeMapper).toDto(existingPackageType);
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
                () -> packageTypeService.getPackageTypeById(999));

        assertEquals("Package type not found with id: 999", exception.getMessage());
        verify(packageTypeRepository).findById(999);
    }

    @Test
    void updatePackageType_WhenValidUpdate_ShouldUpdatePackageType() {
        // Arrange
        Integer id = 1;
        PackageTypeDTO updateDTO = new PackageTypeDTO();
        updateDTO.setName("Updated Package");
        updateDTO.setDescription("Updated description");
        updateDTO.setNumberOfAppointments(5);
        updateDTO.setPrice(new BigDecimal("150.00"));
        updateDTO.setNutritionistRate(new BigDecimal("0.75"));
        updateDTO.setBundle(true);
        updateDTO.setActive(true);

        when(packageTypeRepository.findById(id)).thenReturn(Optional.of(existingPackageType));
        doNothing().when(packageTypeMapper).updatePackageTypeFromDTO(updateDTO, existingPackageType);
        when(packageTypeRepository.save(any(PackageType.class))).thenReturn(existingPackageType);

        // Act
        PackageType updated = packageTypeService.updatePackageType(id, updateDTO);

        // Assert
        assertNotNull(updated);
        verify(packageTypeRepository).findById(id);
        verify(packageTypeMapper).updatePackageTypeFromDTO(updateDTO, existingPackageType);
        verify(packageTypeRepository).save(existingPackageType);
    }

    @Test
    void updatePackageType_WhenNotFound_ShouldThrowException() {
        // Arrange
        Integer id = 999;
        when(packageTypeRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        PackageTypeServiceException exception = assertThrows(
                PackageTypeServiceException.class,
                () -> packageTypeService.updatePackageType(id, validPackageTypeDTO));

        assertEquals("Package type not found with id: 999", exception.getMessage());
        verify(packageTypeRepository).findById(id);
        verify(packageTypeMapper, never()).updatePackageTypeFromDTO(any(), any());
        verify(packageTypeRepository, never()).save(any());
    }

    @Test
    void deactivatePackageType_WhenExists_ShouldDeactivatePackageType() {
        // Arrange
        when(packageTypeRepository.findById(1)).thenReturn(Optional.of(existingPackageType));
        when(packageTypeRepository.save(any(PackageType.class))).thenAnswer(invocation -> {
            PackageType packageType = invocation.getArgument(0);
            // Make sure active is set to false
            assertFalse(packageType.isActive());
            return packageType;
        });

        // Act
        PackageType result = packageTypeService.deactivatePackageType(1);

        // Assert
        assertFalse(result.isActive());
        verify(packageTypeRepository).findById(1);
        verify(packageTypeRepository).save(existingPackageType);
    }

    @Test
    void deactivatePackageType_WhenNotFound_ShouldThrowException() {
        // Arrange
        when(packageTypeRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        PackageTypeServiceException exception = assertThrows(
                PackageTypeServiceException.class,
                () -> packageTypeService.deactivatePackageType(999));

        assertEquals("Package type not found with id: 999", exception.getMessage());
        verify(packageTypeRepository).findById(999);
        verify(packageTypeRepository, never()).save(any());
    }
}