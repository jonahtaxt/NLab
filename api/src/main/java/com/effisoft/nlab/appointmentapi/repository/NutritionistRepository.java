package com.effisoft.nlab.appointmentapi.repository;

import com.effisoft.nlab.appointmentapi.entity.Nutritionist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface NutritionistRepository extends JpaRepository<Nutritionist, Integer> {
    List<Nutritionist> findByIsActiveTrue();
    Optional<Nutritionist> findByEmail(String email);
}
