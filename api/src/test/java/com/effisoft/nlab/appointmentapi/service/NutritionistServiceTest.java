package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.entity.Nutritionist;
import com.effisoft.nlab.appointmentapi.exception.NutritionistServiceException;
import com.effisoft.nlab.appointmentapi.repository.NutritionistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NutritionistServiceTest {

    @Mock
    private NutritionistRepository nutritionistRepository;

    @InjectMocks
    private NutritionistService nutritionistService;

    private Nutritionist nutritionist;

    @BeforeEach
    void setUp() {
        nutritionist = new Nutritionist();
        nutritionist.setFirstName("Jane");
        nutritionist.setLastName("Smith");
        nutritionist.setEmail("jane.smith@example.com");
        nutritionist.setPhone("1234567890");
    }

    @Test
    void createNutritionist_WhenEmailNotExists_ShouldCreateNutritionist() {
        // Arrange
        when(nutritionistRepository.findByEmail(nutritionist.getEmail())).thenReturn(Optional.empty());
        when(nutritionistRepository.save(any(Nutritionist.class))).thenReturn(nutritionist);

        // Act
        Nutritionist createdNutritionist = nutritionistService.createNutritionist(nutritionist);

        // Assert
        assertNotNull(createdNutritionist);
        assertEquals(nutritionist.getEmail(), createdNutritionist.getEmail());
        assertTrue(createdNutritionist.isActive());
        assertNotNull(createdNutritionist.getCreatedAt());
        verify(nutritionistRepository).findByEmail(nutritionist.getEmail());
        verify(nutritionistRepository).save(any(Nutritionist.class));
    }

    @Test
    void createNutritionist_WhenEmailExists_ShouldThrowException() {
        // Arrange
        when(nutritionistRepository.findByEmail(nutritionist.getEmail())).thenReturn(Optional.of(nutritionist));

        // Act & Assert
        NutritionistServiceException exception = assertThrows(
                NutritionistServiceException.class,
                () -> nutritionistService.createNutritionist(nutritionist)
        );

        assertEquals("A Nutritionist with email " + nutritionist.getEmail() + " already exists", exception.getMessage());
        verify(nutritionistRepository).findByEmail(nutritionist.getEmail());
        verify(nutritionistRepository, never()).save(any(Nutritionist.class));
    }

    @Test
    void getAllActiveNutritionists_ShouldReturnOnlyActiveNutritionists() {
        // Arrange
        Nutritionist nutritionist1 = new Nutritionist();
        nutritionist1.setActive(true);
        Nutritionist nutritionist2 = new Nutritionist();
        nutritionist2.setActive(true);
        List<Nutritionist> activeNutritionists = Arrays.asList(nutritionist1, nutritionist2);

        when(nutritionistRepository.findByIsActiveTrue()).thenReturn(activeNutritionists);

        // Act
        List<Nutritionist> result = nutritionistService.getAllActiveNutritionists();

        // Assert
        assertEquals(2, result.size());
        verify(nutritionistRepository).findByIsActiveTrue();
    }

    @Test
    void updateNutritionist_WhenExists_ShouldUpdateNutritionist() {
        // Arrange
        Integer id = 1;
        Nutritionist existingNutritionist = new Nutritionist();
        existingNutritionist.setId(id);
        existingNutritionist.setFirstName("Old Name");

        Nutritionist updatedNutritionist = new Nutritionist();
        updatedNutritionist.setFirstName("New Name");
        updatedNutritionist.setLastName("New Last");
        updatedNutritionist.setPhone("9876543210");

        when(nutritionistRepository.findById(id)).thenReturn(Optional.of(existingNutritionist));
        when(nutritionistRepository.save(any(Nutritionist.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Nutritionist result = nutritionistService.updateNutritionist(id, updatedNutritionist);

        // Assert
        assertEquals(updatedNutritionist.getFirstName(), result.getFirstName());
        assertEquals(updatedNutritionist.getLastName(), result.getLastName());
        assertEquals(updatedNutritionist.getPhone(), result.getPhone());
        verify(nutritionistRepository).findById(id);
        verify(nutritionistRepository).save(any(Nutritionist.class));
    }

    @Test
    void deactivateNutritionist_WhenExists_ShouldDeactivateNutritionist() {
        // Arrange
        Integer id = 1;
        Nutritionist existingNutritionist = new Nutritionist();
        existingNutritionist.setId(id);
        existingNutritionist.setActive(true);

        when(nutritionistRepository.findById(id)).thenReturn(Optional.of(existingNutritionist));
        when(nutritionistRepository.save(any(Nutritionist.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        nutritionistService.deactivateNutritionist(id);

        // Assert
        assertFalse(existingNutritionist.isActive());
        verify(nutritionistRepository).findById(id);
        verify(nutritionistRepository).save(existingNutritionist);
    }
}