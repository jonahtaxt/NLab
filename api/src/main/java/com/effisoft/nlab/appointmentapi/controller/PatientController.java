package com.effisoft.nlab.appointmentapi.controller;

import com.effisoft.nlab.appointmentapi.dto.PatientDTO;
import com.effisoft.nlab.appointmentapi.dto.PageResponseDTO;
import com.effisoft.nlab.appointmentapi.entity.Patient;
import com.effisoft.nlab.appointmentapi.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@Validated
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody PatientDTO patientDTO) {
        Patient createdPatient = patientService.createPatient(patientDTO);
        return new ResponseEntity<>(createdPatient, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")
    public ResponseEntity<Patient> updatePatient(
            @PathVariable Integer id,
            @Valid @RequestBody PatientDTO patientDTO) {
        Patient updatedPatient = patientService.updatePatient(id, patientDTO);
        return ResponseEntity.ok(updatedPatient);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")
    public ResponseEntity<List<Patient>> getAllActivePatients() {
        List<Patient> activePatients = patientService.getAllActivePatients();
        return ResponseEntity.ok(activePatients);
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")
    public ResponseEntity<PageResponseDTO<PatientDTO>> getAllPatients(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) Boolean active) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<PatientDTO> patients = patientService.getPatients(pageable, searchTerm, active);

        PageResponseDTO<PatientDTO> response = new PageResponseDTO<>(
                patients.getContent(),
                patients.getNumber(),
                patients.getSize(),
                patients.getTotalElements(),
                patients.getTotalPages(),
                patients.isFirst(),
                patients.isLast());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")
    public ResponseEntity<Patient> getPatientById(@PathVariable Integer id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")
    public ResponseEntity<Void> deletePatient(@PathVariable Integer id) {
        patientService.deactivatePatient(id);
        return ResponseEntity.noContent().build();
    }
}
