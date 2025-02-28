package com.effisoft.nlab.appointmentapi.repository;

import com.effisoft.nlab.appointmentapi.entity.CardPaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardPaymentTypeRepository extends JpaRepository<CardPaymentType, Integer> {
    List<CardPaymentType> findByActiveTrue();
}