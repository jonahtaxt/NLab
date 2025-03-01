package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.dto.PaymentMethodDTO;
import com.effisoft.nlab.appointmentapi.entity.PaymentMethod;
import com.effisoft.nlab.appointmentapi.exception.PaymentMethodServiceException;
import com.effisoft.nlab.appointmentapi.repository.PaymentMethodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentMethodServiceTest {

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @InjectMocks
    private PaymentMethodService paymentMethodService;

    private PaymentMethodDTO validDTO;
    private PaymentMethod existingPaymentMethod;

    @BeforeEach
    void setUp() {
        // Set up valid DTO
        validDTO = new PaymentMethodDTO();
        validDTO.setName("Credit Card");
        validDTO.setDescription("Payment with credit card");

        // Set up existing payment method
        existingPaymentMethod = new PaymentMethod();
        existingPaymentMethod.setId(1);
        existingPaymentMethod.setName("Credit Card");
        existingPaymentMethod.setDescription("Payment with credit card");
    }

    @Test
    void createPaymentMethod_WhenValidDTO_ShouldCreatePaymentMethod() {
        // Arrange
        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenReturn(existingPaymentMethod);

        // Act
        PaymentMethod created = paymentMethodService.createPaymentMethod(validDTO);

        // Assert
        assertNotNull(created);
        assertEquals(validDTO.getName(), created.getName());
        assertEquals(validDTO.getDescription(), created.getDescription());
        verify(paymentMethodRepository).save(any(PaymentMethod.class));
    }

    @Test
    void createPaymentMethod_WhenDataIntegrityViolation_ShouldThrowException() {
        // Arrange
        when(paymentMethodRepository.save(any(PaymentMethod.class)))
                .thenThrow(new DataIntegrityViolationException("Database error"));

        // Act & Assert
        PaymentMethodServiceException exception = assertThrows(
                PaymentMethodServiceException.class,
                () -> paymentMethodService.createPaymentMethod(validDTO));

        assertEquals(
                "Create Payment Method failed due to data integrity violation",
                exception.getMessage());
    }

    @Test
    void getAllPaymentMethods_ShouldReturnAllPaymentMethods() {
        // Arrange
        List<PaymentMethod> paymentMethods = Arrays.asList(existingPaymentMethod);
        when(paymentMethodRepository.findAll()).thenReturn(paymentMethods);

        // Act
        List<PaymentMethod> result = paymentMethodService.getAllPaymentMethods();

        // Assert
        assertEquals(1, result.size());
        verify(paymentMethodRepository).findAll();
    }

    @Test
    void getPaymentMethodById_WhenExists_ShouldReturnPaymentMethod() {
        // Arrange
        when(paymentMethodRepository.findById(1)).thenReturn(Optional.of(existingPaymentMethod));

        // Act
        PaymentMethod result = paymentMethodService.getPaymentMethodById(1);

        // Assert
        assertNotNull(result);
        assertEquals(existingPaymentMethod.getId(), result.getId());
        verify(paymentMethodRepository).findById(1);
    }

    @Test
    void getPaymentMethodById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(paymentMethodRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        PaymentMethodServiceException exception = assertThrows(
                PaymentMethodServiceException.class,
                () -> paymentMethodService.getPaymentMethodById(999));

        assertEquals("Payment method not found with id: 999", exception.getMessage());
        verify(paymentMethodRepository).findById(999);
    }

    @Test
    void updatePaymentMethod_WhenValidUpdate_ShouldUpdatePaymentMethod() {
        // Arrange
        PaymentMethodDTO updateDTO = new PaymentMethodDTO();
        updateDTO.setName("Updated Name");
        updateDTO.setDescription("Updated Description");

        when(paymentMethodRepository.findById(1)).thenReturn(Optional.of(existingPaymentMethod));
        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenReturn(existingPaymentMethod);

        // Act
        PaymentMethod updated = paymentMethodService.updatePaymentMethod(1, updateDTO);

        // Assert
        assertNotNull(updated);
        assertEquals(updateDTO.getName(), updated.getName());
        assertEquals(updateDTO.getDescription(), updated.getDescription());
        verify(paymentMethodRepository).findById(1);
        verify(paymentMethodRepository).save(any(PaymentMethod.class));
    }

    @Test
    void deletePaymentMethod_WhenExists_ShouldDeletePaymentMethod() {
        // Arrange
        when(paymentMethodRepository.findById(1)).thenReturn(Optional.of(existingPaymentMethod));
        doNothing().when(paymentMethodRepository).delete(any(PaymentMethod.class));

        // Act
        paymentMethodService.deletePaymentMethod(1);

        // Assert
        verify(paymentMethodRepository).findById(1);
        verify(paymentMethodRepository).delete(existingPaymentMethod);
    }

    @Test
    void deletePaymentMethod_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(paymentMethodRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        PaymentMethodServiceException exception = assertThrows(
                PaymentMethodServiceException.class,
                () -> paymentMethodService.deletePaymentMethod(999));

        assertEquals("Payment method not found with id: 999", exception.getMessage());
        verify(paymentMethodRepository).findById(999);
        verify(paymentMethodRepository, never()).delete(any(PaymentMethod.class));
    }
}