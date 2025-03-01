package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.dto.NutritionistPaymentPeriodDTO;
import com.effisoft.nlab.appointmentapi.entity.Appointment;
import com.effisoft.nlab.appointmentapi.entity.Nutritionist;
import com.effisoft.nlab.appointmentapi.entity.NutritionistPaymentPeriod;
import com.effisoft.nlab.appointmentapi.exception.NutritionistPaymentPeriodException;
import com.effisoft.nlab.appointmentapi.repository.AppointmentRepository;
import com.effisoft.nlab.appointmentapi.repository.NutritionistPaymentPeriodRepository;
import com.effisoft.nlab.appointmentapi.repository.NutritionistRepository;
import com.effisoft.nlab.appointmentapi.service.base.ServiceExceptionHandler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class NutritionistPaymentPeriodService {
    private final NutritionistPaymentPeriodRepository nutritionistPaymentPeriodRepository;
    private final NutritionistRepository nutritionistRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional
    public NutritionistPaymentPeriod createPaymentPeriod(@Valid NutritionistPaymentPeriodDTO dto) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
            () -> {
                Nutritionist nutritionist = nutritionistRepository.findById(dto.getNutritionistId())
                    .orElseThrow(() -> new NutritionistPaymentPeriodException("Nutritionist not found"));

                // Validate date range
                if (dto.getPeriodEndDate().isBefore(dto.getPeriodStartDate())) {
                    throw new NutritionistPaymentPeriodException("End date cannot be before start date");
                }

                NutritionistPaymentPeriod paymentPeriod = new NutritionistPaymentPeriod();
                paymentPeriod.setNutritionist(nutritionist);
                paymentPeriod.setPeriodStartDate(dto.getPeriodStartDate());
                paymentPeriod.setPeriodEndDate(dto.getPeriodEndDate());

                // Calculate total appointments and amount based on completed appointments
                LocalDateTime startDateTime = dto.getPeriodStartDate().atStartOfDay();
                LocalDateTime endDateTime = dto.getPeriodEndDate().atTime(23, 59, 59);

                List<Appointment> completedAppointments = appointmentRepository
                        .findByNutritionistIdAndAppointmentDateTimeBetween(
                                nutritionist.getId(),
                                startDateTime,
                                endDateTime
                        )
                        .stream()
                        .filter(appointment -> "COMPLETED".equals(appointment.getStatus()))
                        .toList();

                paymentPeriod.setTotalAppointments(completedAppointments.size());

                // Calculate total amount based on package types' nutritionist rates
                BigDecimal totalAmount = completedAppointments.stream()
                        .map(appointment ->
                                appointment.getPurchasedPackage().getPackageType().getNutritionistRate())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                paymentPeriod.setTotalAmount(totalAmount);
                paymentPeriod.setPaymentStatus("PENDING");

                return nutritionistPaymentPeriodRepository.save(paymentPeriod);
            }, 
            NutritionistPaymentPeriodException::new, 
            "Create Payment Period"
        );
    }

    @Transactional(readOnly = true)
    public List<NutritionistPaymentPeriod> getAllPaymentPeriods() {
        return ServiceExceptionHandler.executeWithExceptionHandling(
            () -> nutritionistPaymentPeriodRepository.findAll(),
            NutritionistPaymentPeriodException::new,
            "Get All Payment Periods"
        );
    }

    @Transactional(readOnly = true)
    public NutritionistPaymentPeriod getPaymentPeriodById(Integer id) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
            () -> nutritionistPaymentPeriodRepository.findById(id)
                .orElseThrow(() -> new NutritionistPaymentPeriodException(
                    "Payment period not found with id: " + id)),
            NutritionistPaymentPeriodException::new,
            "Get Payment Period By Id"
        );
    }

    @Transactional
    public NutritionistPaymentPeriod processPayment(Integer id) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
            () -> {
                NutritionistPaymentPeriod period = getPaymentPeriodById(id);

                if ("PAID".equals(period.getPaymentStatus())) {
                    throw new NutritionistPaymentPeriodException("Payment period is already paid");
                }

                if ("CANCELLED".equals(period.getPaymentStatus())) {
                    throw new NutritionistPaymentPeriodException("Cannot process cancelled payment period");
                }

                period.setPaymentStatus("PAID");
                period.setProcessedDate(LocalDateTime.now());

                return nutritionistPaymentPeriodRepository.save(period);
            },
            NutritionistPaymentPeriodException::new,
            "Process Payment"
        );
    }

    @Transactional
    public NutritionistPaymentPeriod updatePaymentPeriod(Integer id, @Valid NutritionistPaymentPeriodDTO dto) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
            () -> {
                NutritionistPaymentPeriod existingPeriod = getPaymentPeriodById(id);

                if ("PAID".equals(existingPeriod.getPaymentStatus())) {
                    throw new NutritionistPaymentPeriodException("Cannot update paid payment period");
                }

                // Validate date range
                if (dto.getPeriodEndDate().isBefore(dto.getPeriodStartDate())) {
                    throw new NutritionistPaymentPeriodException("End date cannot be before start date");
                }

                existingPeriod.setPeriodStartDate(dto.getPeriodStartDate());
                existingPeriod.setPeriodEndDate(dto.getPeriodEndDate());

                // Recalculate appointments and amount
                LocalDateTime startDateTime = dto.getPeriodStartDate().atStartOfDay();
                LocalDateTime endDateTime = dto.getPeriodEndDate().atTime(23, 59, 59);

                List<Appointment> completedAppointments = appointmentRepository
                        .findByNutritionistIdAndAppointmentDateTimeBetween(
                                existingPeriod.getNutritionist().getId(),
                                startDateTime,
                                endDateTime
                        )
                        .stream()
                        .filter(appointment -> "COMPLETED".equals(appointment.getStatus()))
                        .toList();

                existingPeriod.setTotalAppointments(completedAppointments.size());

                BigDecimal totalAmount = completedAppointments.stream()
                        .map(appointment ->
                                appointment.getPurchasedPackage().getPackageType().getNutritionistRate())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                existingPeriod.setTotalAmount(totalAmount);
                existingPeriod.setPaymentStatus(dto.getPaymentStatus());

                return nutritionistPaymentPeriodRepository.save(existingPeriod);
            },
            NutritionistPaymentPeriodException::new,
            "Update Payment Period"
        );
    }

    @Transactional
    public NutritionistPaymentPeriod cancelPaymentPeriod(Integer id) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
            () -> {
                NutritionistPaymentPeriod period = getPaymentPeriodById(id);

                if ("PAID".equals(period.getPaymentStatus())) {
                    throw new NutritionistPaymentPeriodException("Cannot cancel paid payment period");
                }

                period.setPaymentStatus("CANCELLED");
                return nutritionistPaymentPeriodRepository.save(period);
            },
            NutritionistPaymentPeriodException::new,
            "Cancel Payment Period"
        );
    }
}