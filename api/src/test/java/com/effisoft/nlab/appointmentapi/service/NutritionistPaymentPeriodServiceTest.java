package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.dto.NutritionistPaymentPeriodDTO;
import com.effisoft.nlab.appointmentapi.entity.*;
import com.effisoft.nlab.appointmentapi.exception.NutritionistPaymentPeriodException;
import com.effisoft.nlab.appointmentapi.repository.AppointmentRepository;
import com.effisoft.nlab.appointmentapi.repository.NutritionistPaymentPeriodRepository;
import com.effisoft.nlab.appointmentapi.repository.NutritionistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NutritionistPaymentPeriodServiceTest {

    @Mock
    private NutritionistPaymentPeriodRepository paymentPeriodRepository;

    @Mock
    private NutritionistRepository nutritionistRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private NutritionistPaymentPeriodService paymentPeriodService;

    private NutritionistPaymentPeriodDTO validDTO;
    private NutritionistPaymentPeriod existingPeriod;
    private Nutritionist nutritionist;
    private List<Appointment> COMPLETADAAppointments;

    @BeforeEach
    void setUp() {
        // Set up nutritionist
        nutritionist = new Nutritionist();
        nutritionist.setId(1);
        nutritionist.setFirstName("John");
        nutritionist.setLastName("Doe");
        nutritionist.setEmail("john.doe@example.com");

        // Set up valid DTO
        validDTO = new NutritionistPaymentPeriodDTO();
        validDTO.setNutritionistId(1);
        validDTO.setPeriodStartDate(LocalDate.now().minusDays(30));
        validDTO.setPeriodEndDate(LocalDate.now());
        validDTO.setPaymentStatus("PENDING");

        // Set up COMPLETADA appointments
        PackageType packageType = new PackageType();
        packageType.setNutritionistRate(new BigDecimal("50.00"));

        PurchasedPackage purchasedPackage = new PurchasedPackage();
        purchasedPackage.setPackageType(packageType);

        Appointment appointment = new Appointment();
        appointment.setPurchasedPackage(purchasedPackage);
        appointment.setStatus("COMPLETADA");

        COMPLETADAAppointments = Arrays.asList(appointment);

        // Set up existing period
        existingPeriod = new NutritionistPaymentPeriod();
        existingPeriod.setId(1);
        existingPeriod.setNutritionist(nutritionist);
        existingPeriod.setPeriodStartDate(LocalDate.now().minusDays(30));
        existingPeriod.setPeriodEndDate(LocalDate.now());
        existingPeriod.setTotalAppointments(1);
        existingPeriod.setTotalAmount(new BigDecimal("50.00"));
        existingPeriod.setPaymentStatus("PENDING");
    }

    @Test
    void createPaymentPeriod_WhenValidDTO_ShouldCreatePeriod() {
        // Arrange
        when(nutritionistRepository.findById(1)).thenReturn(Optional.of(nutritionist));
        when(appointmentRepository.findByNutritionistIdAndAppointmentDateTimeBetween(
                eq(1), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(COMPLETADAAppointments);
        when(paymentPeriodRepository.save(any(NutritionistPaymentPeriod.class)))
                .thenReturn(existingPeriod);

        // Act
        NutritionistPaymentPeriod created = paymentPeriodService.createPaymentPeriod(validDTO);

        // Assert
        assertNotNull(created);
        assertEquals("PENDING", created.getPaymentStatus());
        assertEquals(1, created.getTotalAppointments());
        assertEquals(0, new BigDecimal("50.00").compareTo(created.getTotalAmount()));

        verify(nutritionistRepository).findById(1);
        verify(appointmentRepository).findByNutritionistIdAndAppointmentDateTimeBetween(
                eq(1), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(paymentPeriodRepository).save(any(NutritionistPaymentPeriod.class));
    }

    @Test
    void createPaymentPeriod_WhenInvalidDateRange_ShouldThrowException() {
        // Arrange
        validDTO.setPeriodEndDate(validDTO.getPeriodStartDate().minusDays(1));
        when(nutritionistRepository.findById(1)).thenReturn(Optional.of(nutritionist));

        // Act & Assert
        NutritionistPaymentPeriodException exception = assertThrows(
                NutritionistPaymentPeriodException.class,
                () -> paymentPeriodService.createPaymentPeriod(validDTO)
        );

        assertEquals("End date cannot be before start date", exception.getMessage());
    }

    @Test
    void processPayment_WhenPending_ShouldProcessSuccessfully() {
        // Arrange
        when(paymentPeriodRepository.findById(1)).thenReturn(Optional.of(existingPeriod));
        when(paymentPeriodRepository.save(any(NutritionistPaymentPeriod.class)))
                .thenReturn(existingPeriod);

        // Act
        NutritionistPaymentPeriod processed = paymentPeriodService.processPayment(1);

        // Assert
        assertEquals("PAID", processed.getPaymentStatus());
        assertNotNull(processed.getProcessedDate());
        verify(paymentPeriodRepository).save(any(NutritionistPaymentPeriod.class));
    }

    @Test
    void processPayment_WhenAlreadyPaid_ShouldThrowException() {
        // Arrange
        existingPeriod.setPaymentStatus("PAID");
        when(paymentPeriodRepository.findById(1)).thenReturn(Optional.of(existingPeriod));

        // Act & Assert
        NutritionistPaymentPeriodException exception = assertThrows(
                NutritionistPaymentPeriodException.class,
                () -> paymentPeriodService.processPayment(1)
        );

        assertEquals("Payment period is already paid", exception.getMessage());
    }

    @Test
    void cancelPaymentPeriod_WhenPending_ShouldCancelSuccessfully() {
        // Arrange
        when(paymentPeriodRepository.findById(1)).thenReturn(Optional.of(existingPeriod));
        when(paymentPeriodRepository.save(any(NutritionistPaymentPeriod.class)))
                .thenReturn(existingPeriod);

        // Act
        paymentPeriodService.cancelPaymentPeriod(1);

        // Assert
        assertEquals("CANCELADA", existingPeriod.getPaymentStatus());
        verify(paymentPeriodRepository).save(existingPeriod);
    }

    @Test
    void cancelPaymentPeriod_WhenPaid_ShouldThrowException() {
        // Arrange
        existingPeriod.setPaymentStatus("PAID");
        when(paymentPeriodRepository.findById(1)).thenReturn(Optional.of(existingPeriod));

        // Act & Assert
        NutritionistPaymentPeriodException exception = assertThrows(
                NutritionistPaymentPeriodException.class,
                () -> paymentPeriodService.cancelPaymentPeriod(1)
        );

        assertEquals("Cannot cancel paid payment period", exception.getMessage());
    }
}