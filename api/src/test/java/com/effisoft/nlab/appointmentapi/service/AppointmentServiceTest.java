package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.dto.AppointmentDTO;
import com.effisoft.nlab.appointmentapi.entity.*;
import com.effisoft.nlab.appointmentapi.exception.AppointmentServiceException;
import com.effisoft.nlab.appointmentapi.repository.AppointmentRepository;
import com.effisoft.nlab.appointmentapi.repository.NutritionistRepository;
import com.effisoft.nlab.appointmentapi.repository.PurchasedPackageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PurchasedPackageRepository purchasedPackageRepository;

    @Mock
    private NutritionistRepository nutritionistRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private AppointmentDTO validAppointmentDTO;
    private Appointment existingAppointment;
    private PurchasedPackage purchasedPackage;
    private Nutritionist nutritionist;

    @BeforeEach
    void setUp() {
        // Set up nutritionist
        nutritionist = new Nutritionist();
        nutritionist.setId(1);
        nutritionist.setFirstName("Jane");
        nutritionist.setLastName("Doe");
        nutritionist.setEmail("jane.doe@example.com");

        // Set up purchased package
        purchasedPackage = new PurchasedPackage();
        purchasedPackage.setId(1);
        purchasedPackage.setRemainingAppointments(5);
        purchasedPackage.setExpirationDate(LocalDateTime.now().plusDays(1));

        // Set up valid DTO
        validAppointmentDTO = new AppointmentDTO();
        validAppointmentDTO.setPurchasedPackageId(1);
        validAppointmentDTO.setNutritionistId(1);
        validAppointmentDTO.setAppointmentDateTime(LocalDateTime.now().plusDays(1));
        validAppointmentDTO.setStatus("SCHEDULED");
        validAppointmentDTO.setNotes("Initial consultation");

        // Set up existing appointment
        existingAppointment = new Appointment();
        existingAppointment.setId(1);
        existingAppointment.setPurchasedPackage(purchasedPackage);
        existingAppointment.setNutritionist(nutritionist);
        existingAppointment.setAppointmentDateTime(LocalDateTime.now().plusDays(1));
        existingAppointment.setStatus("SCHEDULED");
        existingAppointment.setNotes("Initial consultation");
        existingAppointment.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void scheduleAppointment_WhenValidAppointment_ShouldCreateAppointment() {
        // Arrange
        when(purchasedPackageRepository.findById(1)).thenReturn(Optional.of(purchasedPackage));
        when(nutritionistRepository.findById(1)).thenReturn(Optional.of(nutritionist));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(existingAppointment);

        // Act
        Appointment scheduled = appointmentService.scheduleAppointment(validAppointmentDTO);

        // Assert
        assertNotNull(scheduled);
        assertEquals("SCHEDULED", scheduled.getStatus());
        assertNotNull(scheduled.getCreatedAt());
        verify(purchasedPackageRepository).save(any(PurchasedPackage.class));
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void scheduleAppointment_WhenNoRemainingAppointments_ShouldThrowException() {
        // Arrange
        purchasedPackage.setRemainingAppointments(0);
        when(purchasedPackageRepository.findById(1)).thenReturn(Optional.of(purchasedPackage));

        // Act & Assert
        AppointmentServiceException exception = assertThrows(
                AppointmentServiceException.class,
                () -> appointmentService.scheduleAppointment(validAppointmentDTO)
        );

        assertEquals("No remaining appointments in the package", exception.getMessage());
        verify(nutritionistRepository, never()).findById(any());
    }

    @Test
    void getAppointmentsByNutritionistAndDateRange_ShouldReturnAppointments() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(7);
        List<Appointment> appointments = Arrays.asList(existingAppointment);
        
        when(appointmentRepository.findByNutritionistIdAndAppointmentDateTimeBetween(
                1, startDate, endDate)).thenReturn(appointments);
        when(nutritionistRepository.existsById(1)).thenReturn(true);

        // Act
        List<Appointment> result = appointmentService
                .getAppointmentsByNutritionistAndDateRange(1, startDate, endDate);

        // Assert
        assertEquals(1, result.size());
        verify(appointmentRepository)
                .findByNutritionistIdAndAppointmentDateTimeBetween(1, startDate, endDate);
    }

    @Test
    void updateAppointmentStatus_WhenValidUpdate_ShouldUpdateStatus() {
        // Arrange
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(existingAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(existingAppointment);

        // Act
        Appointment updated = appointmentService.updateAppointmentStatus(1, "COMPLETED");

        // Assert
        assertEquals("COMPLETED", updated.getStatus());
        verify(appointmentRepository).save(existingAppointment);
    }

    @Test
    void updateAppointmentStatus_WhenNotFound_ShouldThrowException() {
        // Arrange
        when(appointmentRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        AppointmentServiceException exception = assertThrows(
                AppointmentServiceException.class,
                () -> appointmentService.updateAppointmentStatus(999, "COMPLETED")
        );

        assertEquals("Appointment not found", exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void cancelAppointment_WhenValid_ShouldCancelAndRestoreAppointment() {
        // Arrange
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(existingAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(existingAppointment);
        when(purchasedPackageRepository.save(any(PurchasedPackage.class))).thenReturn(purchasedPackage);

        // Act
        appointmentService.cancelAppointment(1);

        // Assert
        assertEquals("CANCELLED", existingAppointment.getStatus());
        assertEquals(6, purchasedPackage.getRemainingAppointments()); // Increased by 1
        verify(purchasedPackageRepository).save(purchasedPackage);
        verify(appointmentRepository).save(existingAppointment);
    }

    @Test
    void cancelAppointment_WhenNotFound_ShouldThrowException() {
        // Arrange
        when(appointmentRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        AppointmentServiceException exception = assertThrows(
                AppointmentServiceException.class,
                () -> appointmentService.cancelAppointment(999)
        );

        assertEquals("Appointment not found", exception.getMessage());
        verify(purchasedPackageRepository, never()).save(any(PurchasedPackage.class));
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }
}