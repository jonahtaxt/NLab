package com.effisoft.nlab.appointmentapi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.effisoft.nlab.appointmentapi.dto.AppointmentNotesDTO;
import com.effisoft.nlab.appointmentapi.entity.AppointmentNotes;
import com.effisoft.nlab.appointmentapi.exception.AppointmentNotesException;
import com.effisoft.nlab.appointmentapi.mapper.AppointmentNotesMapper;
import com.effisoft.nlab.appointmentapi.repository.AppointmentNotesRepository;
import com.effisoft.nlab.appointmentapi.service.base.ServiceExceptionHandler;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class AppointmentNotesService {
    private final AppointmentNotesRepository appointmentNotesRepository;
    private final AppointmentNotesMapper appointmentNotesMapper;

    @Transactional
    public AppointmentNotes createAppointmentNotes(@Valid AppointmentNotesDTO dto) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
                () -> {
                    AppointmentNotes appointmentNotes = appointmentNotesMapper.toEntity(dto);
                    return appointmentNotesRepository.save(appointmentNotes);
                }, AppointmentNotesException::new, "Create Appointment Notes");
    }
}
