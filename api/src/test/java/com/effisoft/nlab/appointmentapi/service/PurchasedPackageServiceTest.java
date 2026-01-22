package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.dto.PurchasedPackageDTO;
import com.effisoft.nlab.appointmentapi.entity.*;
import com.effisoft.nlab.appointmentapi.exception.PurchasedPackageServiceException;
import com.effisoft.nlab.appointmentapi.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchasedPackageServiceTest {

    @Mock
    private PurchasedPackageRepository purchasedPackageRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PackageTypeRepository packageTypeRepository;

    @InjectMocks
    private PurchasedPackageService purchasedPackageService;

    private PurchasedPackageDTO validDTO;
    private PurchasedPackage existingPackage;
    private Patient patient;
    private PackageType packageType;

    @BeforeEach
    void setUp() {
        // Set up patient
        patient = new Patient();
        patient.setId(1);
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setEmail("john.doe@example.com");

        // Set up package type
        packageType = new PackageType();
        packageType.setId(1);
        packageType.setName("Basic Package");
        packageType.setNumberOfAppointments(4);
        packageType.setPrice(new BigDecimal("100.00"));

        // Set up valid DTO
        validDTO = new PurchasedPackageDTO();
        validDTO.setPatientId(1);
        validDTO.setPackageTypeId(1);

        // Set up existing package
        existingPackage = new PurchasedPackage();
        existingPackage.setId(1);
        existingPackage.setPatient(patient);
        existingPackage.setPackageType(packageType);
        existingPackage.setPurchaseDate(LocalDateTime.now());
        existingPackage.setRemainingAppointments(4);
        existingPackage.setExpirationDate(LocalDateTime.now().plusMonths(6));
    }

    @Test
    void createPurchasedPackage_WhenValidDTO_ShouldCreatePackage() {
        // Arrange
        when(patientRepository.findById(1)).thenReturn(Optional.of(patient));
        when(packageTypeRepository.findById(1)).thenReturn(Optional.of(packageType));
        when(purchasedPackageRepository.save(any(PurchasedPackage.class))).thenReturn(existingPackage);

        // Act
        PurchasedPackage created = purchasedPackageService.createPurchasedPackage(validDTO);

        // Assert
        assertNotNull(created);
        assertEquals(packageType.getNumberOfAppointments(), created.getRemainingAppointments());
        assertNotNull(created.getPurchaseDate());
        assertNotNull(created.getExpirationDate());

        verify(patientRepository).findById(1);
        verify(packageTypeRepository).findById(1);
        verify(purchasedPackageRepository).save(any(PurchasedPackage.class));
    }

    @Test
    void createPurchasedPackage_WhenPatientNotFound_ShouldThrowException() {
        // Arrange
        when(patientRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        PurchasedPackageServiceException exception = assertThrows(
                PurchasedPackageServiceException.class,
                () -> purchasedPackageService.createPurchasedPackage(validDTO));

        assertEquals("Patient not found", exception.getMessage());
        verify(patientRepository).findById(1);
        verify(purchasedPackageRepository, never()).save(any(PurchasedPackage.class));
    }

    @Test
    void createPurchasedPackage_WhenDataIntegrityViolation_ShouldThrowException() {
        // Arrange
        when(patientRepository.findById(1)).thenReturn(Optional.of(patient));
        when(packageTypeRepository.findById(1)).thenReturn(Optional.of(packageType));
        when(purchasedPackageRepository.save(any(PurchasedPackage.class)))
                .thenThrow(new DataIntegrityViolationException("Database error"));

        // Act & Assert
        PurchasedPackageServiceException exception = assertThrows(
                PurchasedPackageServiceException.class,
                () -> purchasedPackageService.createPurchasedPackage(validDTO));

        assertEquals(
                "Create Purchased Package failed due to data integrity violation",
                exception.getMessage());
    }

    @Test
    void getAllPurchasedPackages_ShouldReturnAllPackages() {
        // Arrange
        List<PurchasedPackage> packages = Arrays.asList(existingPackage);
        when(purchasedPackageRepository.findAll()).thenReturn(packages);

        // Act
        List<PurchasedPackage> result = purchasedPackageService.getAllPurchasedPackages();

        // Assert
        assertEquals(1, result.size());
        verify(purchasedPackageRepository).findAll();
    }

    @Test
    void getPurchasedPackageById_WhenExists_ShouldReturnPackage() {
        // Arrange
        when(purchasedPackageRepository.findById(1)).thenReturn(Optional.of(existingPackage));

        // Act
        PurchasedPackage result = purchasedPackageService.getPurchasedPackageById(1);

        // Assert
        assertNotNull(result);
        assertEquals(existingPackage.getId(), result.getId());
        verify(purchasedPackageRepository).findById(1);
    }

    @Test
    void getPurchasedPackageById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(purchasedPackageRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        PurchasedPackageServiceException exception = assertThrows(
                PurchasedPackageServiceException.class,
                () -> purchasedPackageService.getPurchasedPackageById(999));

        assertEquals("Purchased package not found with id: 999", exception.getMessage());
        verify(purchasedPackageRepository).findById(999);
    }

    @Test
    void updatePurchasedPackage_WhenValidUpdate_ShouldUpdatePackage() {
        // Arrange
        PurchasedPackageDTO updateDTO = new PurchasedPackageDTO();
        updateDTO.setRemainingAppointments(2);
        updateDTO.setExpirationDate(LocalDateTime.now().plusMonths(3));

        when(purchasedPackageRepository.findById(1)).thenReturn(Optional.of(existingPackage));
        when(purchasedPackageRepository.save(any(PurchasedPackage.class))).thenReturn(existingPackage);

        // Act
        PurchasedPackage updated = purchasedPackageService.updatePurchasedPackage(1, updateDTO);

        // Assert
        assertNotNull(updated);
        assertEquals(updateDTO.getRemainingAppointments(), updated.getRemainingAppointments());
        assertEquals(updateDTO.getExpirationDate(), updated.getExpirationDate());
        verify(purchasedPackageRepository).findById(1);
        verify(purchasedPackageRepository).save(any(PurchasedPackage.class));
    }

    @Test
    void isPackageValid_WhenValidPackage_ShouldReturnTrue() {
        // Arrange
        when(purchasedPackageRepository.findById(1)).thenReturn(Optional.of(existingPackage));

        // Act
        boolean isValid = purchasedPackageService.isPackageValid(1);

        // Assert
        assertTrue(isValid);
        verify(purchasedPackageRepository).findById(1);
    }

    @Test
    void isPackageValid_WhenExpired_ShouldReturnFalse() {
        // Arrange
        existingPackage.setExpirationDate(LocalDateTime.now().minusDays(1));
        when(purchasedPackageRepository.findById(1)).thenReturn(Optional.of(existingPackage));

        // Act
        boolean isValid = purchasedPackageService.isPackageValid(1);

        // Assert
        assertFalse(isValid);
        verify(purchasedPackageRepository).findById(1);
    }

    @Test
    void isPackageValid_WhenNoRemainingAppointments_ShouldReturnFalse() {
        // Arrange
        existingPackage.setRemainingAppointments(0);
        when(purchasedPackageRepository.findById(1)).thenReturn(Optional.of(existingPackage));

        // Act
        boolean isValid = purchasedPackageService.isPackageValid(1);

        // Assert
        assertFalse(isValid);
        verify(purchasedPackageRepository).findById(1);
    }
}