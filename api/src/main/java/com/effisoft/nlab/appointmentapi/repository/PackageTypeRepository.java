package com.effisoft.nlab.appointmentapi.repository;

import com.effisoft.nlab.appointmentapi.entity.PackageType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PackageTypeRepository extends JpaRepository<PackageType, Integer> {
       List<PackageType> findByActiveTrue();

       @Query("SELECT p FROM PackageType p WHERE " +
                     "(:searchTerm IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
                     "(:active IS NULL OR p.active = :active)")
       Page<PackageType> findPackageTypes(
                     @Param("searchTerm") String searchTerm,
                     @Param("active") Boolean active,
                     Pageable pageable);
}
