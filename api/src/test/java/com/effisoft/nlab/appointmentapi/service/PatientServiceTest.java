package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.dto.PatientDTO;
import com.effisoft.nlab.appointmentapi.entity.Patient;
import com.effisoft.nlab.appointmentapi.exception.PatientServiceException;
import com.effisoft.nlab.appointmentapi.repository.PatientRepository;
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
public class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private PatientDTO patientDTO;
    private Patient patient;

    @BeforeEach
    void setUp() {
        // Initialize test data
        patientDTO = new PatientDTO();
        patientDTO.setFirstName("John");
        patientDTO.setLastName("Doe");
        patientDTO.setEmail("john.doe@example.com");
        patientDTO.setPhone("1234567890");

        patient = new Patient();
        patient.setId(1);
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setEmail("john.doe@example.com");
        patient.setPhone("1234567890");
        patient.setCreatedAt(LocalDateTime.now());
        patient.setActive(true);
    }

    @Test
    void createPatient_WhenEmailNotExists_ShouldCreatePatient() {
        // Arrange
        when(patientRepository.findByEmail(patientDTO.getEmail())).thenReturn(Optional.empty());
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        // Act
        Patient createdPatient = patientService.createPatient(patientDTO);

        // Assert
        assertNotNull(createdPatient);
        assertEquals(patientDTO.getEmail(), createdPatient.getEmail());
        assertTrue(createdPatient.isActive());
        assertNotNull(createdPatient.getCreatedAt());
        verify(patientRepository).findByEmail(patientDTO.getEmail());
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void createPatient_WhenEmailExists_ShouldThrowException() {
        // Arrange
        when(patientRepository.findByEmail(patientDTO.getEmail())).thenReturn(Optional.of(patient));

        // Act & Assert
        PatientServiceException exception = assertThrows(
                PatientServiceException.class,
                () -> patientService.createPatient(patientDTO)
        );

        assertEquals(
                String.format("A Patient with email %s already exists", patientDTO.getEmail()),
                exception.getMessage()
        );
        verify(patientRepository).findByEmail(patientDTO.getEmail());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void createPatient_WhenDataIntegrityViolation_ShouldThrowException() {
        // Arrange
        when(patientRepository.findByEmail(patientDTO.getEmail())).thenReturn(Optional.empty());
        when(patientRepository.save(any(Patient.class)))
                .thenThrow(new DataIntegrityViolationException("Database error"));

        // Act & Assert
        PatientServiceException exception = assertThrows(
                PatientServiceException.class,
                () -> patientService.createPatient(patientDTO)
        );

        assertEquals(
                "Failed to create patient due to data integrity violation",
                exception.getMessage()
        );
    }

    @Test
    void getAllActivePatients_ShouldReturnOnlyActivePatients() {
        // Arrange
        Patient patient2 = new Patient();
        patient2.setActive(true);
        List<Patient> activePatients = Arrays.asList(patient, patient2);

        when(patientRepository.findByActiveTrue()).thenReturn(activePatients);

        // Act
        List<Patient> result = patientService.getAllActivePatients();

        // Assert
        assertEquals(2, result.size());
        verify(patientRepository).findByActiveTrue();
    }

    @Test
    void getPatientById_WhenExists_ShouldReturnPatient() {
        // Arrange
        when(patientRepository.findById(1)).thenReturn(Optional.of(patient));

        // Act
        Patient result = patientService.getPatientById(1);

        // Assert
        assertNotNull(result);
        assertEquals(patient.getId(), result.getId());
        verify(patientRepository).findById(1);
    }

    @Test
    void getPatientById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(patientRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        PatientServiceException exception = assertThrows(
                PatientServiceException.class,
                () -> patientService.getPatientById(1)
        );

        assertEquals("Patient not found with id: 1", exception.getMessage());
        verify(patientRepository).findById(1);
    }

    @Test
    void updatePatient_WhenExists_ShouldUpdatePatient() {
        // Arrange
        PatientDTO updatedDTO = new PatientDTO();
        updatedDTO.setFirstName("Jane");
        updatedDTO.setLastName("Smith");
        updatedDTO.setEmail("jane.smith@example.com");
        updatedDTO.setPhone("9876543210");

        when(patientRepository.findById(1)).thenReturn(Optional.of(patient));
        when(patientRepository.findByEmail(updatedDTO.getEmail())).thenReturn(Optional.empty());
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        // Act
        Patient updatedPatient = patientService.updatePatient(1, updatedDTO);

        // Assert
        assertEquals(updatedDTO.getFirstName(), updatedPatient.getFirstName());
        assertEquals(updatedDTO.getLastName(), updatedPatient.getLastName());
        assertEquals(updatedDTO.getEmail(), updatedPatient.getEmail());
        assertEquals(updatedDTO.getPhone(), updatedPatient.getPhone());
        verify(patientRepository).findById(1);
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void updatePatient_WhenEmailAlreadyExists_ShouldThrowException() {
        // Arrange
        PatientDTO updatedDTO = new PatientDTO();
        updatedDTO.setEmail("existing@example.com");

        Patient existingPatient = new Patient();
        existingPatient.setEmail("existing@example.com");

        when(patientRepository.findById(1)).thenReturn(Optional.of(patient));
        when(patientRepository.findByEmail(updatedDTO.getEmail())).thenReturn(Optional.of(existingPatient));

        // Act & Assert
        PatientServiceException exception = assertThrows(
                PatientServiceException.class,
                () -> patientService.updatePatient(1, updatedDTO)
        );

        assertEquals("Email is already in use: existing@example.com", exception.getMessage());
    }

    @Test
    void deactivatePatient_WhenExists_ShouldDeactivatePatient() {
        // Arrange
        when(patientRepository.findById(1)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        // Act
        patientService.deactivatePatient(1);

        // Assert
        assertFalse(patient.isActive());
        verify(patientRepository).findById(1);
        verify(patientRepository).save(patient);
    }

    @Test
    void deactivatePatient_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(patientRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        PatientServiceException exception = assertThrows(
                PatientServiceException.class,
                () -> patientService.deactivatePatient(1)
        );

        assertEquals("Patient not found with id: 1", exception.getMessage());
        verify(patientRepository).findById(1);
        verify(patientRepository, never()).save(any(Patient.class));
    }
}