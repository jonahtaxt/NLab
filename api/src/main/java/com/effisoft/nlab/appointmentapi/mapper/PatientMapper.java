package com.effisoft.nlab.appointmentapi.mapper;

import com.effisoft.nlab.appointmentapi.dto.PatientDTO;
import com.effisoft.nlab.appointmentapi.entity.Patient;

import java.util.List;

import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PatientMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", expression = "java(dto.getId() != null && dto.getId() > 0 ? dto.getId() : null)")
    Patient toEntity(PatientDTO dto);

    PatientDTO toDto(Patient entity);

    List<PatientDTO> toDtoList(List<Patient> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updatePatientFromDTO(PatientDTO dto, @MappingTarget Patient entity);

    @AfterMapping
    default void setDefaults(@MappingTarget Patient patient) {
        // Set default values or perform additional validations
        if (patient.getEmail() != null) {
            patient.setEmail(patient.getEmail().toLowerCase().trim());
        }
    }
}