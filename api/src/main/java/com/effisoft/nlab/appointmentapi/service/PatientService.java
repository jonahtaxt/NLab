package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.entity.Patient;
import com.effisoft.nlab.appointmentapi.exception.PatientServiceException;
import com.effisoft.nlab.appointmentapi.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;

    @Transactional
    public Patient createPatient(Patient patient) {
        if (patientRepository.findByEmail(patient.getEmail()).isPresent()) {
            throw new PatientServiceException("A Patient with email " + patient.getEmail() + " already exists");
        }

        patient.setCreatedAt(LocalDateTime.now());
        patient.setActive(true);
        return patientRepository.save(patient);
    }

    @Transactional(readOnly = true)
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Transactional
    public Patient updatePatient(Integer id, Patient updatedPatient) {
        return patientRepository.findById(id)
                .map(existingPatient -> {
                    existingPatient.setFirstName(updatedPatient.getFirstName());
                    existingPatient.setLastName(updatedPatient.getLastName());
                    existingPatient.setPhone(updatedPatient.getPhone());
                    return patientRepository.save(existingPatient);
                })
                .orElseThrow(() -> new PatientServiceException("Patient not found"));
    }

    @Transactional
    public void deactivatePatient(Integer id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientServiceException("Patient not found"));

        patient.setActive(false);
        patientRepository.save(patient);
    }
}
