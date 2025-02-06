package com.effisoft.nlab.appointmentapi.repository;

import com.effisoft.nlab.appointmentapi.entity.CardPaymentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardPaymentTypeRepository extends JpaRepository<CardPaymentType, Integer> {
}
