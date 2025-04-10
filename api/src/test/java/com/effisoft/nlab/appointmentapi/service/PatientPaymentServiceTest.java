package com.effisoft.nlab.appointmentapi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.effisoft.nlab.appointmentapi.dto.PatientPaymentDTO;
import com.effisoft.nlab.appointmentapi.dto.PatientPurchasedPackageDTO;
import com.effisoft.nlab.appointmentapi.entity.CardPaymentType;
import com.effisoft.nlab.appointmentapi.entity.PatientPayment;
import com.effisoft.nlab.appointmentapi.entity.PaymentMethod;
import com.effisoft.nlab.appointmentapi.entity.PurchasedPackage;
import com.effisoft.nlab.appointmentapi.entity.PackageType;
import com.effisoft.nlab.appointmentapi.entity.Patient;
import com.effisoft.nlab.appointmentapi.exception.PatientPaymentException;
import com.effisoft.nlab.appointmentapi.mapper.PurchasedPackageMapper;
import com.effisoft.nlab.appointmentapi.repository.CardPaymentTypeRepository;
import com.effisoft.nlab.appointmentapi.repository.PatientPaymentRepository;
import com.effisoft.nlab.appointmentapi.repository.PaymentMethodRepository;

@ExtendWith(MockitoExtension.class)
class PatientPaymentServiceTest {

    @Mock
    private PatientPaymentRepository patientPaymentRepository;

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @Mock
    private CardPaymentTypeRepository cardPaymentTypeRepository;

    @Mock
    private PurchasedPackageService purchasedPackageService;

    @Mock
    private PurchasedPackageMapper purchasedPackageMapper;

    @InjectMocks
    private PatientPaymentService patientPaymentService;

    private PatientPaymentDTO paymentDTO;
    private PaymentMethod paymentMethod;
    private CardPaymentType cardPaymentType;
    private PurchasedPackage purchasedPackage;
    private PatientPurchasedPackageDTO patientPurchasedPackageDTO;

    @BeforeEach
    void setUp() {
        // Setup test data
        paymentDTO = new PatientPaymentDTO();
        paymentDTO.setPurchasedPackageId(1);
        paymentDTO.setPaymentMethodId(1);
        paymentDTO.setCardPaymentTypeId(1);
        paymentDTO.setTotalPaid(new BigDecimal("100.00"));

        paymentMethod = new PaymentMethod();
        paymentMethod.setId(1);
        paymentMethod.setName("Tarjeta");

        cardPaymentType = new CardPaymentType();
        cardPaymentType.setId(1);
        cardPaymentType.setName("Visa");

        PackageType packageType = new PackageType();
        packageType.setId(1);
        packageType.setPrice(new BigDecimal("200.00"));

        Patient patient = new Patient();
        patient.setId(1);

        purchasedPackage = new PurchasedPackage();
        purchasedPackage.setId(1);
        purchasedPackage.setPackageType(packageType);
        purchasedPackage.setPatient(patient);
        purchasedPackage.setPaidInFull(false);

        patientPurchasedPackageDTO = new PatientPurchasedPackageDTO();
        patientPurchasedPackageDTO.setPurchasedPackage(purchasedPackage);
        
        // Set empty payments list to initialize packagePaidTotal to zero
        patientPurchasedPackageDTO.setPatientPayments(Arrays.asList());
    }

    @Test
    void createPatientPayment_Success() {
        // Arrange
        when(purchasedPackageService.getPatientPurchasedPackageByPackageId(any()))
            .thenReturn(patientPurchasedPackageDTO);
        when(paymentMethodRepository.findById(any()))
            .thenReturn(Optional.of(paymentMethod));
        when(cardPaymentTypeRepository.findById(any()))
            .thenReturn(Optional.of(cardPaymentType));
        when(patientPaymentRepository.save(any()))
            .thenReturn(new PatientPayment());

        // Act
        PatientPayment result = patientPaymentService.createPatientPayment(paymentDTO);

        // Assert
        assertNotNull(result);
        verify(purchasedPackageService).getPatientPurchasedPackageByPackageId(1);
        verify(paymentMethodRepository).findById(1);
        verify(cardPaymentTypeRepository).findById(1);
        verify(patientPaymentRepository).save(any());
    }

    @Test
    void createPatientPayment_WithCashPayment() {
        // Arrange
        paymentMethod.setName("Efectivo");
        paymentDTO.setCardPaymentTypeId(null);

        when(purchasedPackageService.getPatientPurchasedPackageByPackageId(any()))
            .thenReturn(patientPurchasedPackageDTO);
        when(paymentMethodRepository.findById(any()))
            .thenReturn(Optional.of(paymentMethod));
        when(patientPaymentRepository.save(any()))
            .thenReturn(new PatientPayment());

        // Act
        PatientPayment result = patientPaymentService.createPatientPayment(paymentDTO);

        // Assert
        assertNotNull(result);
        verify(purchasedPackageService).getPatientPurchasedPackageByPackageId(1);
        verify(paymentMethodRepository).findById(1);
        verify(cardPaymentTypeRepository, never()).findById(any());
        verify(patientPaymentRepository).save(any());
    }

    @Test
    void createPatientPayment_PackageNotFound() {
        // Arrange
        when(purchasedPackageService.getPatientPurchasedPackageByPackageId(any()))
            .thenReturn(null);

        // Act & Assert
        assertThrows(PatientPaymentException.class, () -> {
            patientPaymentService.createPatientPayment(paymentDTO);
        });
    }

    @Test
    void createPatientPayment_PaymentMethodNotFound() {
        // Arrange
        when(purchasedPackageService.getPatientPurchasedPackageByPackageId(any()))
            .thenReturn(patientPurchasedPackageDTO);
        when(paymentMethodRepository.findById(any()))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PatientPaymentException.class, () -> {
            patientPaymentService.createPatientPayment(paymentDTO);
        });
    }

    @Test
    void createPatientPayment_CardPaymentTypeNotFound() {
        // Arrange
        when(purchasedPackageService.getPatientPurchasedPackageByPackageId(any()))
            .thenReturn(patientPurchasedPackageDTO);
        when(paymentMethodRepository.findById(any()))
            .thenReturn(Optional.of(paymentMethod));
        when(cardPaymentTypeRepository.findById(any()))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PatientPaymentException.class, () -> {
            patientPaymentService.createPatientPayment(paymentDTO);
        });
    }

    @Test
    void createPatientPayment_ExceedsPackageTotal() {
        // Arrange
        paymentDTO.setTotalPaid(new BigDecimal("300.00"));
        when(purchasedPackageService.getPatientPurchasedPackageByPackageId(any()))
            .thenReturn(patientPurchasedPackageDTO);
        when(paymentMethodRepository.findById(any()))
            .thenReturn(Optional.of(paymentMethod));
        when(cardPaymentTypeRepository.findById(any()))
            .thenReturn(Optional.of(cardPaymentType));

        // Act & Assert
        assertThrows(PatientPaymentException.class, () -> {
            patientPaymentService.createPatientPayment(paymentDTO);
        });
    }

    @Test
    void getPatientPaymentsByPurchasedPackageId_Success() {
        // Arrange
        List<PatientPayment> expectedPayments = Arrays.asList(new PatientPayment(), new PatientPayment());
        when(patientPaymentRepository.findByPurchasedPackageId(any()))
            .thenReturn(expectedPayments);

        // Act
        List<PatientPayment> result = patientPaymentService.getPatientPaymentsByPurchasedPackageId(1);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(patientPaymentRepository).findByPurchasedPackageId(1);
    }

    @Test
    void getPatientPaymentsByPurchasedPackageId_EmptyList() {
        // Arrange
        when(patientPaymentRepository.findByPurchasedPackageId(any()))
            .thenReturn(Arrays.asList());

        // Act
        List<PatientPayment> result = patientPaymentService.getPatientPaymentsByPurchasedPackageId(1);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(patientPaymentRepository).findByPurchasedPackageId(1);
    }
} 