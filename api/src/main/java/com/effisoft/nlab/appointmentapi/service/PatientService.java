package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.dto.PatientDTO;
import com.effisoft.nlab.appointmentapi.entity.Patient;
import com.effisoft.nlab.appointmentapi.exception.PatientServiceException;
import com.effisoft.nlab.appointmentapi.mapper.PatientMapper;
import com.effisoft.nlab.appointmentapi.repository.PatientRepository;
import com.effisoft.nlab.appointmentapi.service.base.ServiceExceptionHandler;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Transactional
    public Patient createPatient(@Valid PatientDTO dto) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
                () -> {
                    // Validate email uniqueness
                    if (patientRepository.findByEmail(dto.getEmail().toLowerCase()).isPresent()) {
                        throw new PatientServiceException(
                                String.format("Patient with email %s already exists", dto.getEmail()));
                    }

                    // Create and sanitize new patient
                    Patient patient = patientMapper.toEntity(dto);
                    patient.setCreatedAt(LocalDateTime.now());
                    patient.setActive(true);

                    return patientRepository.save(patient);
                }, PatientServiceException::new, "Create Patient");
    }

    @Transactional(readOnly = true)
    public List<Patient> getAllPatients() {
        return ServiceExceptionHandler.executeWithExceptionHandling(
                () -> patientRepository.findAll(),
                PatientServiceException::new,
                "Get All Patients");
    }

    @Transactional(readOnly = true)
    public List<Patient> getAllActivePatients() {
        return ServiceExceptionHandler.executeWithExceptionHandling(
                () -> patientRepository.findByActiveTrue(),
                PatientServiceException::new,
                "Get All Active Patients");
    }

    @Transactional(readOnly = true)
    public Patient getPatientById(Integer id) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
                () -> findById(id),
                PatientServiceException::new,
                "Get Patient by ID");
    }

    @Transactional
    public Patient updatePatient(Integer id, @Valid PatientDTO dto) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
                () -> {
                    Patient existingPatient = findById(id);

                    // Check email uniqueness if it's being changed
                    if (!existingPatient.getEmail().equals(dto.getEmail())) {
                        if (patientRepository.findByEmail(dto.getEmail().toLowerCase()).isPresent()) {
                            throw new PatientServiceException("Email is already in use: " + dto.getEmail());
                        }
                    }

                    patientMapper.updatePatientFromDTO(dto, existingPatient);
                    existingPatient.setUpdatedAt(LocalDateTime.now());
                    return patientRepository.save(existingPatient);
                }, PatientServiceException::new, "Update Patient");
    }

    @Transactional
    public Patient deactivatePatient(Integer id) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
                () -> {
                    Patient patient = findById(id);

                    patient.setActive(false);
                    return patientRepository.save(patient);
                }, PatientServiceException::new, "Deactivate Patient");
    }

    @Transactional(readOnly = true)
    public Page<PatientDTO> getPatients(Pageable pageable, String searchTerm, Boolean active) {
        return patientRepository.findPatients(searchTerm, active, pageable)
                .map(patientMapper::toDto);
    }

    private Patient findById(Integer id) {

        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientServiceException("Patient not found with id: " + id));
    }
}
