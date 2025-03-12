package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.dto.NutritionistDTO;
import com.effisoft.nlab.appointmentapi.entity.Nutritionist;
import com.effisoft.nlab.appointmentapi.exception.NutritionistServiceException;
import com.effisoft.nlab.appointmentapi.mapper.NutritionistMapper;
import com.effisoft.nlab.appointmentapi.repository.NutritionistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NutritionistServiceTest {

    @Mock
    private NutritionistRepository nutritionistRepository;
    
    @Mock
    private NutritionistMapper nutritionistMapper;

    @InjectMocks
    private NutritionistService nutritionistService;

    private NutritionistDTO validNutritionistDTO;
    private Nutritionist existingNutritionist;
    private Nutritionist mappedNutritionist;

    @BeforeEach
    void setUp() {
        // Set up a valid DTO for testing
        validNutritionistDTO = new NutritionistDTO();
        validNutritionistDTO.setFirstName("Jane");
        validNutritionistDTO.setLastName("Smith");
        validNutritionistDTO.setEmail("jane.smith@example.com");
        validNutritionistDTO.setPhone("1234567890");
        validNutritionistDTO.setActive(true);

        // Set up an existing nutritionist
        existingNutritionist = new Nutritionist();
        existingNutritionist.setId(1);
        existingNutritionist.setFirstName("Jane");
        existingNutritionist.setLastName("Smith");
        existingNutritionist.setEmail("jane.smith@example.com");
        existingNutritionist.setPhone("1234567890");
        existingNutritionist.setCreatedAt(LocalDateTime.now());
        existingNutritionist.setActive(true);
        
        // Set up a mapped nutritionist (result of the mapper operation)
        mappedNutritionist = new Nutritionist();
        mappedNutritionist.setFirstName("Jane");
        mappedNutritionist.setLastName("Smith");
        mappedNutritionist.setEmail("jane.smith@example.com");
        mappedNutritionist.setPhone("1234567890");
        // Note: CreatedAt and Active will be set by the service
    }

    @Test
    void createNutritionist_WhenValidDTOAndEmailNotExists_ShouldCreateNutritionist() {
        // Arrange
        when(nutritionistRepository.findByEmail(validNutritionistDTO.getEmail().toLowerCase())).thenReturn(Optional.empty());
        when(nutritionistMapper.toEntity(validNutritionistDTO)).thenReturn(mappedNutritionist);
        when(nutritionistRepository.save(any(Nutritionist.class))).thenAnswer(invocation -> {
            Nutritionist saved = invocation.getArgument(0);
            saved.setId(1);
            return saved;
        });

        // Act
        Nutritionist created = nutritionistService.createNutritionist(validNutritionistDTO);

        // Assert
        assertNotNull(created);
        assertEquals(validNutritionistDTO.getEmail(), created.getEmail());
        assertTrue(created.isActive());
        assertNotNull(created.getCreatedAt());
        verify(nutritionistRepository).findByEmail(validNutritionistDTO.getEmail().toLowerCase());
        verify(nutritionistMapper).toEntity(validNutritionistDTO);
        verify(nutritionistRepository).save(any(Nutritionist.class));
    }

    @Test
    void createNutritionist_WhenEmailExists_ShouldThrowException() {
        // Arrange
        when(nutritionistRepository.findByEmail(validNutritionistDTO.getEmail().toLowerCase()))
                .thenReturn(Optional.of(existingNutritionist));

        // Act & Assert
        NutritionistServiceException exception = assertThrows(
                NutritionistServiceException.class,
                () -> nutritionistService.createNutritionist(validNutritionistDTO));

        assertEquals(
                String.format("Nutritionist with email %s already exists", validNutritionistDTO.getEmail()),
                exception.getMessage());
        verify(nutritionistRepository).findByEmail(validNutritionistDTO.getEmail().toLowerCase());
        verify(nutritionistMapper, never()).toEntity(any());
        verify(nutritionistRepository, never()).save(any(Nutritionist.class));
    }

    @Test
    void createNutritionist_WhenDataIntegrityViolation_ShouldThrowException() {
        // Arrange
        when(nutritionistRepository.findByEmail(validNutritionistDTO.getEmail().toLowerCase())).thenReturn(Optional.empty());
        when(nutritionistMapper.toEntity(validNutritionistDTO)).thenReturn(mappedNutritionist);
        when(nutritionistRepository.save(any(Nutritionist.class)))
                .thenThrow(new DataIntegrityViolationException("Database error"));

        // Act & Assert
        NutritionistServiceException exception = assertThrows(
                NutritionistServiceException.class,
                () -> nutritionistService.createNutritionist(validNutritionistDTO));

        assertEquals("Create Nutritionist failed due to data integrity violation", exception.getMessage());
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof DataIntegrityViolationException);
        verify(nutritionistRepository).findByEmail(validNutritionistDTO.getEmail().toLowerCase());
        verify(nutritionistMapper).toEntity(validNutritionistDTO);
        verify(nutritionistRepository).save(any(Nutritionist.class));
    }

    @Test
    void updateNutritionist_WhenValidUpdate_ShouldUpdateNutritionist() {
        // Arrange
        Integer id = 1;
        NutritionistDTO updateDTO = new NutritionistDTO();
        updateDTO.setFirstName("Janet");
        updateDTO.setLastName("Smith-Jones");
        updateDTO.setEmail("janet.smith@example.com");
        updateDTO.setPhone("9876543210");

        when(nutritionistRepository.findById(id)).thenReturn(Optional.of(existingNutritionist));
        when(nutritionistRepository.findByEmail(updateDTO.getEmail().toLowerCase())).thenReturn(Optional.empty());
        doNothing().when(nutritionistMapper).updateNutritionistFromDTO(updateDTO, existingNutritionist);
        when(nutritionistRepository.save(any(Nutritionist.class))).thenReturn(existingNutritionist);

        // Act
        Nutritionist updated = nutritionistService.updateNutritionist(id, updateDTO);

        // Assert
        assertNotNull(updated);
        verify(nutritionistRepository).findById(id);
        verify(nutritionistRepository).findByEmail(updateDTO.getEmail().toLowerCase());
        verify(nutritionistMapper).updateNutritionistFromDTO(updateDTO, existingNutritionist);
        verify(nutritionistRepository).save(existingNutritionist);
    }

    @Test
    void getAllActiveNutritionists_ShouldReturnOnlyActiveNutritionists() {
        // Arrange
        List<Nutritionist> activeNutritionists = Arrays.asList(
                existingNutritionist,
                createNutritionistWithEmail("another@example.com"));
        when(nutritionistRepository.findByActiveTrue()).thenReturn(activeNutritionists);

        // Act
        List<Nutritionist> result = nutritionistService.getAllActiveNutritionists();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(Nutritionist::isActive));
        verify(nutritionistRepository).findByActiveTrue();
    }

    @Test
    void deactivateNutritionist_WhenExists_ShouldDeactivateNutritionist() {
        // Arrange
        Integer id = 1;
        when(nutritionistRepository.findById(id)).thenReturn(Optional.of(existingNutritionist));
        when(nutritionistRepository.save(any(Nutritionist.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        nutritionistService.deactivateNutritionist(id);

        // Assert
        assertFalse(existingNutritionist.isActive());
        verify(nutritionistRepository).findById(id);
        verify(nutritionistRepository).save(existingNutritionist);
    }

    @Test
    void deactivateNutritionist_WhenNotExists_ShouldThrowException() {
        // Arrange
        Integer id = 999;
        when(nutritionistRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        NutritionistServiceException exception = assertThrows(
                NutritionistServiceException.class,
                () -> nutritionistService.deactivateNutritionist(id));

        assertEquals("Nutritionist not found with id: " + id, exception.getMessage());
        verify(nutritionistRepository).findById(id);
        verify(nutritionistRepository, never()).save(any(Nutritionist.class));
    }

    // Helper method to create test nutritionists
    private Nutritionist createNutritionistWithEmail(String email) {
        Nutritionist nutritionist = new Nutritionist();
        nutritionist.setFirstName("Test");
        nutritionist.setLastName("User");
        nutritionist.setEmail(email);
        nutritionist.setPhone("1234567890");
        nutritionist.setCreatedAt(LocalDateTime.now());
        nutritionist.setActive(true);
        return nutritionist;
    }
}