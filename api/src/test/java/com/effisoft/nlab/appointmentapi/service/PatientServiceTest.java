package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.dto.PatientDTO;
import com.effisoft.nlab.appointmentapi.entity.Patient;
import com.effisoft.nlab.appointmentapi.exception.PatientServiceException;
import com.effisoft.nlab.appointmentapi.mapper.PatientMapper;
import com.effisoft.nlab.appointmentapi.repository.PatientRepository;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private PatientService patientService;

    private PatientDTO validPatientDTO;
    private Patient existingPatient;
    private Patient mappedPatient;

    @BeforeEach
    void setUp() {
        // Setup valid DTO
        validPatientDTO = new PatientDTO();
        validPatientDTO.setFirstName("John");
        validPatientDTO.setLastName("Doe");
        validPatientDTO.setEmail("john.doe@example.com");
        validPatientDTO.setPhone("1234567890");
        validPatientDTO.setActive(true);

        // Setup existing patient
        existingPatient = new Patient();
        existingPatient.setId(1);
        existingPatient.setFirstName("John");
        existingPatient.setLastName("Doe");
        existingPatient.setEmail("john.doe@example.com");
        existingPatient.setPhone("1234567890");
        existingPatient.setCreatedAt(LocalDateTime.now());
        existingPatient.setActive(true);

        // Setup mapper
        mappedPatient = new Patient();
        existingPatient.setFirstName("John");
        existingPatient.setLastName("Doe");
        existingPatient.setEmail("john.doe@example.com");
        existingPatient.setPhone("1234567890");
        existingPatient.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createPatient_WhenValidDTOAndEmailNotExists_ShouldCreatePatient() {
        // Arrange
        when(patientRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(patientMapper.toEntity(validPatientDTO)).thenReturn(mappedPatient);
        when(patientRepository.save(any(Patient.class))).thenReturn(existingPatient);

        // Act
        Patient created = patientService.createPatient(validPatientDTO);

        // Assert
        assertNotNull(created);
        assertEquals(validPatientDTO.getEmail(), created.getEmail());
        assertTrue(created.isActive());
        assertNotNull(created.getCreatedAt());
        verify(patientRepository).findByEmail(validPatientDTO.getEmail().toLowerCase());
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void createPatient_WhenEmailExists_ShouldThrowException() {
        // Arrange
        when(patientRepository.findByEmail(anyString())).thenReturn(Optional.of(existingPatient));

        // Act & Assert
        PatientServiceException exception = assertThrows(
                PatientServiceException.class,
                () -> patientService.createPatient(validPatientDTO));

        assertEquals(
                String.format("Patient with email %s already exists", validPatientDTO.getEmail()),
                exception.getMessage());
        verify(patientRepository).findByEmail(validPatientDTO.getEmail().toLowerCase());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void createPatient_WhenDataIntegrityViolation_ShouldThrowException() {
        // Arrange
        when(patientRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(patientMapper.toEntity(validPatientDTO)).thenReturn(mappedPatient);
        when(patientRepository.save(any(Patient.class)))
                .thenThrow(new DataIntegrityViolationException("Database error"));

        // Act & Assert
        PatientServiceException exception = assertThrows(
                PatientServiceException.class,
                () -> patientService.createPatient(validPatientDTO));

        assertEquals("Create Patient failed due to data integrity violation", exception.getMessage());
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof DataIntegrityViolationException);
    }

    @Test
    void getAllActivePatients_ShouldReturnOnlyActivePatients() {
        // Arrange
        List<Patient> activePatients = Arrays.asList(existingPatient);
        when(patientRepository.findByActiveTrue()).thenReturn(activePatients);

        // Act
        List<Patient> result = patientService.getAllActivePatients();

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
        verify(patientRepository).findByActiveTrue();
    }

    @Test
    void getPatientById_WhenExists_ShouldReturnPatient() {
        // Arrange
        when(patientRepository.findById(1)).thenReturn(Optional.of(existingPatient));

        // Act
        Patient result = patientService.getPatientById(1);

        // Assert
        assertNotNull(result);
        assertEquals(existingPatient.getId(), result.getId());
        verify(patientRepository).findById(1);
    }

    @Test
    void getPatientById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(patientRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        PatientServiceException exception = assertThrows(
                PatientServiceException.class,
                () -> patientService.getPatientById(999));

        assertEquals("Patient not found with id: 999", exception.getMessage());
        verify(patientRepository).findById(999);
    }

    @Test
    void updatePatient_WhenValidUpdate_ShouldUpdatePatient() {
        // Arrange
        PatientDTO updateDTO = new PatientDTO();
        updateDTO.setFirstName("Jane");
        updateDTO.setLastName("Smith");
        updateDTO.setEmail("jane.smith@example.com");
        updateDTO.setPhone("9876543210");

        when(patientRepository.findById(1)).thenReturn(Optional.of(existingPatient));
        when(patientRepository.findByEmail(updateDTO.getEmail())).thenReturn(Optional.empty());
        when(patientRepository.save(any(Patient.class))).thenReturn(existingPatient);

        // Act
        Patient updated = patientService.updatePatient(1, updateDTO);

        // Assert
        assertNotNull(updated);
        verify(patientMapper).updatePatientFromDTO(eq(updateDTO), eq(existingPatient));
        verify(patientRepository).findById(1);
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void updatePatient_WhenEmailExistsForDifferentPatient_ShouldThrowException() {
        // Arrange
        PatientDTO updateDTO = new PatientDTO();
        updateDTO.setEmail("existing@example.com");

        Patient existingWithEmail = new Patient();
        existingWithEmail.setId(2);
        existingWithEmail.setEmail("existing@example.com");

        when(patientRepository.findById(1)).thenReturn(Optional.of(existingPatient));
        when(patientRepository.findByEmail(updateDTO.getEmail())).thenReturn(Optional.of(existingWithEmail));

        // Act & Assert
        PatientServiceException exception = assertThrows(
                PatientServiceException.class,
                () -> patientService.updatePatient(1, updateDTO));

        assertEquals("Email is already in use: existing@example.com", exception.getMessage());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void deactivatePatient_WhenExists_ShouldDeactivatePatient() {
        // Arrange
        when(patientRepository.findById(1)).thenReturn(Optional.of(existingPatient));
        when(patientRepository.save(any(Patient.class))).thenReturn(existingPatient);

        // Act
        Patient deactivated = patientService.deactivatePatient(1);

        // Assert
        assertFalse(deactivated.isActive());
        verify(patientRepository).findById(1);
        verify(patientRepository).save(existingPatient);
    }

    @Test
    void getPatients_ShouldReturnPagedResults() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Patient> patients = Arrays.asList(existingPatient);
        Page<Patient> patientPage = new PageImpl<>(patients, pageable, patients.size());
        
        when(patientRepository.findPatients(anyString(), any(), any(Pageable.class)))
                .thenReturn(patientPage);
        when(patientMapper.toDto(any(Patient.class))).thenReturn(validPatientDTO);
        
        // Act
        Page<PatientDTO> result = patientService.getPatients(pageable, "search", true);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(patientRepository).findPatients(eq("search"), eq(true), eq(pageable));
    }
}