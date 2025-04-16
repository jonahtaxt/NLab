package com.effisoft.nlab.appointmentapi.repository;

import com.effisoft.nlab.appointmentapi.entity.PatientAppointmentView;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PatientAppointmentViewRepository extends JpaRepository<PatientAppointmentView, Integer> {

    Page<PatientAppointmentView> findByPatientId(Integer patientId, Pageable pageable);

    List<PatientAppointmentView> findByNutritionistId(Integer nutritionistId);

    List<PatientAppointmentView> findByAppointmentDate(LocalDate date);

    @Query("SELECT p FROM PatientAppointmentView p WHERE p.patientId = :patientId AND p.appointmentDate BETWEEN :startDate AND :endDate")
    List<PatientAppointmentView> findByPatientIdAndDateRange(
            @Param("patientId") Integer patientId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}