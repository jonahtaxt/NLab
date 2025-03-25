package com.effisoft.nlab.appointmentapi.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.effisoft.nlab.appointmentapi.dto.PatientPaymentDTO;
import com.effisoft.nlab.appointmentapi.entity.PatientPayment;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PatientPaymentMapper {

    @Mapping(target = "paymentDate", ignore = true)
    PatientPayment toEntity(PatientPaymentDTO dto);

    PatientPaymentDTO tDto(PatientPayment entity);

    List<PatientPaymentDTO> toDtoList(List<PatientPayment> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "paymentDate", ignore = true)
    void updatePatientPaymentFromDTO(PatientPaymentDTO dto, @MappingTarget PatientPayment entity);
}
