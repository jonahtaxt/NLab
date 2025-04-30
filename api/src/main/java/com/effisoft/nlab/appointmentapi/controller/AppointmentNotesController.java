package com.effisoft.nlab.appointmentapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.effisoft.nlab.appointmentapi.dto.AppointmentNotesDTO;
import com.effisoft.nlab.appointmentapi.entity.AppointmentNotes;
import com.effisoft.nlab.appointmentapi.service.AppointmentNotesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/appointment-notes")
@RequiredArgsConstructor
public class AppointmentNotesController {
    private final AppointmentNotesService appointmentNotesService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")
    public ResponseEntity<AppointmentNotes> createAppointmentNotes(@RequestBody AppointmentNotesDTO dto) {
        AppointmentNotes appointmentNotes = appointmentNotesService.createAppointmentNotes(dto);

        return ResponseEntity.ok(appointmentNotes);
    }
}