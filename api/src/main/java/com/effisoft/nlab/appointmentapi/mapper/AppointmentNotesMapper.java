package com.effisoft.nlab.appointmentapi.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import com.effisoft.nlab.appointmentapi.dto.AppointmentNotesDTO;
import com.effisoft.nlab.appointmentapi.entity.AppointmentNotes;
import com.effisoft.nlab.appointmentapi.repository.AppointmentRepository;

import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class AppointmentNotesMapper {

    @Autowired
    protected AppointmentRepository appointmentRepository;

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "appointment", expression = "java(appointmentRepository.findById(dto.getAppointmentId()).orElseThrow(() -> new RuntimeException(\"Appointment not found\")))")
    @Mapping(target = "id", expression = "java(dto.getId() != null && dto.getId() > 0 ? dto.getId() : null)")
    public abstract AppointmentNotes toEntity(AppointmentNotesDTO dto);

    public abstract AppointmentNotesDTO toDto(AppointmentNotes entity);

    public abstract List<AppointmentNotesDTO> toDtoList(List<AppointmentNotes> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract void updateAppointmentNotesFromDTO(AppointmentNotesDTO dto, @MappingTarget AppointmentNotes entity);
}
