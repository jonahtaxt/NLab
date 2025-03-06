package com.effisoft.nlab.appointmentapi.mapper;

import java.util.List;

import org.mapstruct.*;

import com.effisoft.nlab.appointmentapi.dto.PackageTypeDTO;
import com.effisoft.nlab.appointmentapi.entity.PackageType;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PackageTypeMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PackageType toEntity(PackageTypeDTO dto);

    PackageTypeDTO toDto(PackageType entity);

    List<PackageTypeDTO> toDtoList(List<PackageType> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updatePackageTypeFromDTO(PackageTypeDTO dto, @MappingTarget PackageType entity);

    @AfterMapping
    default void setDefaults(@MappingTarget PackageType patient) {
    }
}
