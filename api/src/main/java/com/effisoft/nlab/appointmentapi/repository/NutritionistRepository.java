package com.effisoft.nlab.appointmentapi.repository;

import com.effisoft.nlab.appointmentapi.entity.Nutritionist;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NutritionistRepository extends JpaRepository<Nutritionist, Integer> {
       List<Nutritionist> findByActiveTrue();

       Optional<Nutritionist> findByEmail(String email);

       @Query("SELECT p FROM Nutritionist p WHERE " +
                     "(:searchTerm IS NULL OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(p.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
                     "(:active IS NULL OR p.active = :active)")
       Page<Nutritionist> findNutritionists(
                     @Param("searchTerm") String searchTerm,
                     @Param("active") Boolean active,
                     Pageable pageable);
}
