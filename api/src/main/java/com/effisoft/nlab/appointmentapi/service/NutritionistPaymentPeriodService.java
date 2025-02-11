package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.entity.Appointment;
import com.effisoft.nlab.appointmentapi.entity.NutritionistPaymentPeriod;
import com.effisoft.nlab.appointmentapi.exception.NutritionistPaymentPeriodException;
import com.effisoft.nlab.appointmentapi.repository.AppointmentRepository;
import com.effisoft.nlab.appointmentapi.repository.NutritionistPaymentPeriodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NutritionistPaymentPeriodService {
    private final NutritionistPaymentPeriodRepository nutritionistPaymentPeriodRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional
    public NutritionistPaymentPeriod createPaymentPeriod(NutritionistPaymentPeriod paymentPeriod) {
        // Calculate total appointments and amount based on completed appointments
        LocalDateTime startDateTime = paymentPeriod.getPeriodStartDate().atStartOfDay();
        LocalDateTime endDateTime = paymentPeriod.getPeriodEndDate().atTime(23, 59, 59);

        List<Appointment> completedAppointments = appointmentRepository
                .findByNutritionistIdAndAppointmentDateTimeBetween(
                        paymentPeriod.getNutritionist().getId(),
                        startDateTime,
                        endDateTime
                )
                .stream()
                .filter(appointment -> "COMPLETED".equals(appointment.getStatus()))
                .toList();

        // Set total appointments
        paymentPeriod.setTotalAppointments(completedAppointments.size());

        // Calculate total amount based on package types' nutritionist rates
        BigDecimal totalAmount = completedAppointments.stream()
                .map(appointment ->
                        appointment.getPurchasedPackage().getPackageType().getNutritionistRate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        paymentPeriod.setTotalAmount(totalAmount);
        paymentPeriod.setPaymentStatus("PENDING");

        return nutritionistPaymentPeriodRepository.save(paymentPeriod);
    }

    @Transactional(readOnly = true)
    public List<NutritionistPaymentPeriod> getAllPaymentPeriods() {
        return nutritionistPaymentPeriodRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<NutritionistPaymentPeriod> getPaymentPeriodById(Integer id) {
        return nutritionistPaymentPeriodRepository.findById(id);
    }

    @Transactional
    public NutritionistPaymentPeriod processPayment(Integer id) {
        return nutritionistPaymentPeriodRepository.findById(id)
                .map(period -> {
                    period.setPaymentStatus("PAID");
                    period.setProcessedDate(LocalDateTime.now());
                    return nutritionistPaymentPeriodRepository.save(period);
                })
                .orElseThrow(() -> new NutritionistPaymentPeriodException("Payment Period not found"));
    }

    @Transactional
    public NutritionistPaymentPeriod updatePaymentPeriod(Integer id, NutritionistPaymentPeriod updatedPeriod) {
        return nutritionistPaymentPeriodRepository.findById(id)
                .map(existingPeriod -> {
                    // Only allow updating certain fields
                    existingPeriod.setPeriodStartDate(updatedPeriod.getPeriodStartDate());
                    existingPeriod.setPeriodEndDate(updatedPeriod.getPeriodEndDate());
                    existingPeriod.setPaymentStatus(updatedPeriod.getPaymentStatus());
                    return nutritionistPaymentPeriodRepository.save(existingPeriod);
                })
                .orElseThrow(() -> new NutritionistPaymentPeriodException("Payment Period not found"));
    }
}