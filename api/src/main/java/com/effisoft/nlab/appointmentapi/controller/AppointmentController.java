package com.effisoft.nlab.appointmentapi.controller;

import com.effisoft.nlab.appointmentapi.dto.AppointmentDTO;
import com.effisoft.nlab.appointmentapi.dto.PageResponseDTO;
import com.effisoft.nlab.appointmentapi.entity.Appointment;
import com.effisoft.nlab.appointmentapi.entity.PatientAppointmentView;
import com.effisoft.nlab.appointmentapi.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")
    public ResponseEntity<Appointment> scheduleAppointment(
            @Valid @RequestBody AppointmentDTO appointmentDTO) {
        Appointment AGENDADAAppointment = appointmentService.scheduleAppointment(appointmentDTO);
        return new ResponseEntity<>(AGENDADAAppointment, HttpStatus.CREATED);
    }

    @GetMapping("/nutritionist/{nutritionistId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")
    public ResponseEntity<List<Appointment>> getAppointmentsByNutritionist(
            @PathVariable Integer nutritionistId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        List<Appointment> appointments = appointmentService
                .getAppointmentsByNutritionistAndDateRange(nutritionistId, startDate, endDate);
        return ResponseEntity.ok(appointments);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")
    public ResponseEntity<Appointment> updateAppointment(
            @Valid @RequestBody AppointmentDTO appointmentDTO) {
        Appointment updatedAppointment = appointmentService.updateAppointmentStatus(appointmentDTO);
        return ResponseEntity.ok(updatedAppointment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Integer id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")
    public ResponseEntity<PageResponseDTO<PatientAppointmentView>> getPatientAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "appointmentDate") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @PathVariable Integer patientId) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<PatientAppointmentView> patientAppointments = appointmentService.getPatientAppointments(pageable,
                patientId);

        PageResponseDTO<PatientAppointmentView> response = new PageResponseDTO<>(
                patientAppointments.getContent(),
                patientAppointments.getNumber(),
                patientAppointments.getSize(),
                patientAppointments.getTotalElements(),
                patientAppointments.getTotalPages(),
                patientAppointments.isFirst(),
                patientAppointments.isLast());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{appointmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Integer appointmentId) {
        Optional<Appointment> appointment = appointmentService.getByAppointmentId(appointmentId);
        return ResponseEntity.ok(appointment.orElse(null));
    }
}