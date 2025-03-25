package com.effisoft.nlab.appointmentapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.effisoft.nlab.appointmentapi.dto.PatientPaymentDTO;
import com.effisoft.nlab.appointmentapi.entity.PatientPayment;
import com.effisoft.nlab.appointmentapi.service.PatientPaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/patient-payment")
@RequiredArgsConstructor
public class PatientPaymentController {
    private final PatientPaymentService patientPaymentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")
    public ResponseEntity<PatientPayment> createPatientPayment(
        @Valid @RequestBody PatientPaymentDTO patientPaymentDTO
    ) {
        PatientPayment createdPatientPayment = patientPaymentService.createPatientPayment(patientPaymentDTO);
        return new ResponseEntity<>(createdPatientPayment, HttpStatus.CREATED);
    }

}
