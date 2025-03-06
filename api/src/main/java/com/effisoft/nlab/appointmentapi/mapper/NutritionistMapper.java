package com.effisoft.nlab.appointmentapi.mapper;

import java.util.List;

import org.mapstruct.*;

import com.effisoft.nlab.appointmentapi.dto.NutritionistDTO;
import com.effisoft.nlab.appointmentapi.entity.Nutritionist;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NutritionistMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Nutritionist toEntity(NutritionistDTO dto);

    NutritionistDTO toDto(Nutritionist entity);

    List<NutritionistDTO> toDtoList(List<Nutritionist> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateNutritionistFromDTO(NutritionistDTO dto, @MappingTarget Nutritionist entity);

    @AfterMapping
    default void setDefaults(@MappingTarget Nutritionist nutritionist) {
    }
}
