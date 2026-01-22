package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.dto.CardPaymentTypeDTO;
import com.effisoft.nlab.appointmentapi.entity.CardPaymentType;
import com.effisoft.nlab.appointmentapi.exception.CardPaymentTypeServiceException;
import com.effisoft.nlab.appointmentapi.repository.CardPaymentTypeRepository;
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
class CardPaymentTypeServiceTest {

    @Mock
    private CardPaymentTypeRepository cardPaymentTypeRepository;

    @InjectMocks
    private CardPaymentTypeService cardPaymentTypeService;

    private CardPaymentTypeDTO validDTO;
    private CardPaymentType existingType;

    @BeforeEach
    void setUp() {
        // Set up valid DTO
        validDTO = new CardPaymentTypeDTO();
        validDTO.setName("3 Month Plan");
        validDTO.setDescription("3 months installment plan");
        validDTO.setBankFeePercentage(new BigDecimal("2.5"));
        validDTO.setNumberOfInstallments(3);
        validDTO.setActive(true);

        // Set up existing type
        existingType = new CardPaymentType();
        existingType.setId(1);
        existingType.setName("3 Month Plan");
        existingType.setDescription("3 months installment plan");
        existingType.setBankFeePercentage(new BigDecimal("2.5"));
        existingType.setNumberOfInstallments(3);
        existingType.setActive(true);
    }

    @Test
    void createCardPaymentType_WhenValidDTO_ShouldCreateType() {
        // Arrange
        when(cardPaymentTypeRepository.save(any(CardPaymentType.class))).thenReturn(existingType);

        // Act
        CardPaymentType created = cardPaymentTypeService.createCardPaymentType(validDTO);

        // Assert
        assertNotNull(created);
        assertEquals(validDTO.getName(), created.getName());
        assertEquals(validDTO.getBankFeePercentage(), created.getBankFeePercentage());
        assertTrue(created.isActive());
        verify(cardPaymentTypeRepository).save(any(CardPaymentType.class));
    }

    @Test
    void createCardPaymentType_WhenDataIntegrityViolation_ShouldThrowException() {
        // Arrange
        when(cardPaymentTypeRepository.save(any(CardPaymentType.class)))
                .thenThrow(new DataIntegrityViolationException("Database error"));

        // Act & Assert
        CardPaymentTypeServiceException exception = assertThrows(
                CardPaymentTypeServiceException.class,
                () -> cardPaymentTypeService.createCardPaymentType(validDTO));

        assertEquals(
                "Create Card Payment Type failed due to data integrity violation",
                exception.getMessage());
    }

    @Test
    void getAllActiveCardPaymentTypes_ShouldReturnOnlyActiveTypes() {
        // Arrange
        List<CardPaymentType> activeTypes = Arrays.asList(existingType);
        when(cardPaymentTypeRepository.findByActiveTrue()).thenReturn(activeTypes);

        // Act
        List<CardPaymentType> result = cardPaymentTypeService.getAllActiveCardPaymentTypes();

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
        verify(cardPaymentTypeRepository).findByActiveTrue();
    }

    @Test
    void getCardPaymentTypeById_WhenExists_ShouldReturnType() {
        // Arrange
        when(cardPaymentTypeRepository.findById(1)).thenReturn(Optional.of(existingType));

        // Act
        CardPaymentType result = cardPaymentTypeService.getCardPaymentTypeById(1);

        // Assert
        assertNotNull(result);
        assertEquals(existingType.getId(), result.getId());
        verify(cardPaymentTypeRepository).findById(1);
    }

    @Test
    void getCardPaymentTypeById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(cardPaymentTypeRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        CardPaymentTypeServiceException exception = assertThrows(
                CardPaymentTypeServiceException.class,
                () -> cardPaymentTypeService.getCardPaymentTypeById(999));

        assertEquals("Card payment type not found with id: 999", exception.getMessage());
        verify(cardPaymentTypeRepository).findById(999);
    }

    @Test
    void updateCardPaymentType_WhenValidUpdate_ShouldUpdateType() {
        // Arrange
        Integer id = 1;
        CardPaymentTypeDTO updateDTO = new CardPaymentTypeDTO();
        updateDTO.setName("6 Month Plan");
        updateDTO.setDescription("6 months installment plan");
        updateDTO.setBankFeePercentage(new BigDecimal("4.5"));
        updateDTO.setNumberOfInstallments(6);

        when(cardPaymentTypeRepository.findById(id)).thenReturn(Optional.of(existingType));
        when(cardPaymentTypeRepository.save(any(CardPaymentType.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CardPaymentType updated = cardPaymentTypeService.updateCardPaymentType(id, updateDTO);

        // Assert
        assertNotNull(updated);
        assertEquals(updateDTO.getName(), updated.getName());
        assertEquals(updateDTO.getDescription(), updated.getDescription());
        assertEquals(updateDTO.getBankFeePercentage(), updated.getBankFeePercentage());
        assertEquals(updateDTO.getNumberOfInstallments(), updated.getNumberOfInstallments());
        verify(cardPaymentTypeRepository).findById(id);
        verify(cardPaymentTypeRepository).save(any(CardPaymentType.class));
    }

    @Test
    void updateCardPaymentType_WhenNotExists_ShouldThrowException() {
        // Arrange
        Integer id = 999;
        when(cardPaymentTypeRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        CardPaymentTypeServiceException exception = assertThrows(
                CardPaymentTypeServiceException.class,
                () -> cardPaymentTypeService.updateCardPaymentType(id, validDTO));

        assertEquals("Card payment type not found with id: " + id, exception.getMessage());
        verify(cardPaymentTypeRepository).findById(id);
        verify(cardPaymentTypeRepository, never()).save(any(CardPaymentType.class));
    }

    @Test
    void updateCardPaymentType_WhenDataIntegrityViolation_ShouldThrowException() {
        // Arrange
        Integer id = 1;
        when(cardPaymentTypeRepository.findById(id)).thenReturn(Optional.of(existingType));
        when(cardPaymentTypeRepository.save(any(CardPaymentType.class)))
                .thenThrow(new DataIntegrityViolationException("Database error"));

        // Act & Assert
        CardPaymentTypeServiceException exception = assertThrows(
                CardPaymentTypeServiceException.class,
                () -> cardPaymentTypeService.updateCardPaymentType(id, validDTO));

        assertEquals(
                "Update Card Payment Type failed due to data integrity violation",
                exception.getMessage());
    }

    @Test
    void deactivateCardPaymentType_WhenExists_ShouldDeactivateType() {
        // Arrange
        Integer id = 1;
        when(cardPaymentTypeRepository.findById(id)).thenReturn(Optional.of(existingType));
        when(cardPaymentTypeRepository.save(any(CardPaymentType.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        cardPaymentTypeService.deactivateCardPaymentType(id);

        // Assert
        assertFalse(existingType.isActive());
        verify(cardPaymentTypeRepository).findById(id);
        verify(cardPaymentTypeRepository).save(existingType);
    }

    @Test
    void deactivateCardPaymentType_WhenNotExists_ShouldThrowException() {
        // Arrange
        Integer id = 999;
        when(cardPaymentTypeRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        CardPaymentTypeServiceException exception = assertThrows(
                CardPaymentTypeServiceException.class,
                () -> cardPaymentTypeService.deactivateCardPaymentType(id));

        assertEquals("Card payment type not found with id: 999", exception.getMessage());
        verify(cardPaymentTypeRepository).findById(id);
        verify(cardPaymentTypeRepository, never()).save(any(CardPaymentType.class));
    }

    @Test
    void deactivateCardPaymentType_WhenSaveError_ShouldThrowException() {
        // Arrange
        Integer id = 1;
        when(cardPaymentTypeRepository.findById(id)).thenReturn(Optional.of(existingType));
        when(cardPaymentTypeRepository.save(any(CardPaymentType.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        CardPaymentTypeServiceException exception = assertThrows(
                CardPaymentTypeServiceException.class,
                () -> cardPaymentTypeService.deactivateCardPaymentType(id));

        assertEquals("Deactivate Card Payment Type failed due to unexpected error", exception.getMessage());
        verify(cardPaymentTypeRepository).findById(id);
        verify(cardPaymentTypeRepository).save(any(CardPaymentType.class));
    }
}