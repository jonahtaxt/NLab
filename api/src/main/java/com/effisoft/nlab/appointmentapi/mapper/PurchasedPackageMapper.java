package com.effisoft.nlab.appointmentapi.mapper;

import java.util.List;

import org.mapstruct.*;

import com.effisoft.nlab.appointmentapi.dto.PurchasedPackageDTO;
import com.effisoft.nlab.appointmentapi.entity.PurchasedPackage;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PurchasedPackageMapper {

    @Mapping(target = "id", expression = "java(dto.getId() != null && dto.getId() > 0 ? dto.getId() : null)")
    PurchasedPackage toEntity(PurchasedPackageDTO dto);

    PurchasedPackageDTO toDto(PurchasedPackage entity);

    List<PurchasedPackageDTO> toDtoList(List<PurchasedPackage> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePurchasedPackageFromDTO(PurchasedPackageDTO dto, @MappingTarget PurchasedPackage entity);

    @AfterMapping
    default void setDefaults(@MappingTarget PurchasedPackage purchasedPackage) {
        if (purchasedPackage.getPurchaseDate() == null) {
            purchasedPackage.setPurchaseDate(java.time.LocalDateTime.now());
        }
        if (purchasedPackage.getRemainingAppointments() == null) {
            purchasedPackage.setRemainingAppointments(0);
        }
    }
}
