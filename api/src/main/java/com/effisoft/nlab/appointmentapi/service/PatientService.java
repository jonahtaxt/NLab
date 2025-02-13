package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.dto.PatientDTO;
import com.effisoft.nlab.appointmentapi.entity.Patient;
import com.effisoft.nlab.appointmentapi.exception.PatientServiceException;
import com.effisoft.nlab.appointmentapi.repository.PatientRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;

    @Transactional
    public Patient createPatient(@Valid PatientDTO dto) {
        try {
            if (patientRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new PatientServiceException(
                        String.format("A Patient with email %s already exists", dto.getEmail())
                );
            }

            // Create and sanitize new patient
            Patient patient = new Patient();
            updatePatientFromDTO(patient, dto);
            patient.setCreatedAt(LocalDateTime.now());
            patient.setActive(true);

            return patientRepository.save(patient);
        } catch (PatientServiceException e) {
            if(e.getMessage().equals(String.format("A Patient with email %s already exists", dto.getEmail()))) {
                throw e;
            } else {
                throw new PatientServiceException("Unexpected error while creating patient", e);
            }
        } catch (DataIntegrityViolationException e) {
            throw new PatientServiceException("Failed to create patient due to data integrity violation", e);
        } catch (Exception e) {
            throw new PatientServiceException("Unexpected error while creating patient", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Patient> getAllActivePatients() {
        return patientRepository.findByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public Patient getPatientById(Integer id) {
        try {
            return findById(id);
        } catch (PatientServiceException pex) {
            if(pex.getMessage().equals("Patient not found with id: " + id)) {
                throw pex;
            } else {
                throw new PatientServiceException("Unexpected error while creating patient", pex);
            }
        } catch (Exception e) {
            throw new PatientServiceException("Failed to deactivate patient", e);
        }
    }

    @Transactional
    public Patient updatePatient(Integer id, @Valid PatientDTO dto) {
        try {
            Patient existingPatient = findById(id);

            //Check email uniqueness if it's being changed
            if(!existingPatient.getEmail().equals(dto.getEmail())) {
                if(patientRepository.findByEmail(dto.getEmail().toLowerCase()).isPresent()) {
                    throw new PatientServiceException("Email is already in use: " + dto.getEmail());
                }
            }

            updatePatientFromDTO(existingPatient, dto);
            return patientRepository.save(existingPatient);
        } catch (DataIntegrityViolationException e) {
            throw new PatientServiceException("Failed to update patient due to data integrity violation", e);
        }
    }

    @Transactional
    public void deactivatePatient(Integer id) {
        try {
            Patient patient = findById(id);

            patient.setActive(false);
            patientRepository.save(patient);
        } catch (PatientServiceException pex) {
            if(pex.getMessage().equals("Patient not found with id: " + id)) {
                throw pex;
            } else {
                throw new PatientServiceException("Unexpected error while creating patient", pex);
            }
        } catch (Exception e) {
            throw new PatientServiceException("Failed to deactivate patient", e);
        }
    }

    private void updatePatientFromDTO(Patient patient, PatientDTO dto) {
        patient.setFirstName(HtmlUtils.htmlEscape(dto.getFirstName().trim()));
        patient.setLastName(HtmlUtils.htmlEscape(dto.getLastName().trim()));
        patient.setEmail(dto.getEmail().toLowerCase().trim());
        patient.setPhone(dto.getPhone() != null ? dto.getPhone().trim() : null);
    }

    private Patient findById(Integer id) {

        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientServiceException("Patient not found with id: " + id));
    }
}
