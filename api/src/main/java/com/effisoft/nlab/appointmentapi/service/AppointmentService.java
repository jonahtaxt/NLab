package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.dto.AppointmentDTO;
import com.effisoft.nlab.appointmentapi.entity.Appointment;
import com.effisoft.nlab.appointmentapi.entity.Nutritionist;
import com.effisoft.nlab.appointmentapi.entity.PurchasedPackage;
import com.effisoft.nlab.appointmentapi.exception.AppointmentServiceException;
import com.effisoft.nlab.appointmentapi.repository.AppointmentRepository;
import com.effisoft.nlab.appointmentapi.repository.NutritionistRepository;
import com.effisoft.nlab.appointmentapi.repository.PurchasedPackageRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Validated
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PurchasedPackageRepository purchasedPackageRepository;
    private final NutritionistRepository nutritionistRepository;

    private static final Set<String> VALID_STATUSES = Set.of(
            "SCHEDULED", "COMPLETED", "CANCELLED", "RESCHEDULED", "NO_SHOW"
    );

    @Transactional
    public Appointment scheduleAppointment(@Valid AppointmentDTO dto) {
        try {
            // Validate and get purchased package
            PurchasedPackage purchasedPackage = purchasedPackageRepository
                    .findById(dto.getPurchasedPackageId())
                    .orElseThrow(() -> new AppointmentServiceException("Purchased package not found"));

            // Check if package has remaining appointments
            if (purchasedPackage.getRemainingAppointments() <= 0) {
                throw new AppointmentServiceException("No remaining appointments in the package");
            }

            // Check if package is expired
            if (purchasedPackage.getExpirationDate().isBefore(LocalDateTime.now())) {
                throw new AppointmentServiceException("Package has expired");
            }

            // Validate and get nutritionist
            Nutritionist nutritionist = nutritionistRepository
                    .findById(dto.getNutritionistId())
                    .orElseThrow(() -> new AppointmentServiceException("Nutritionist not found"));

            // Validate appointment time is in the future
            if (dto.getAppointmentDateTime().isBefore(LocalDateTime.now())) {
                throw new AppointmentServiceException("Appointment time must be in the future");
            }

            // Create new appointment
            Appointment appointment = new Appointment();
            appointment.setPurchasedPackage(purchasedPackage);
            appointment.setNutritionist(nutritionist);
            appointment.setAppointmentDateTime(dto.getAppointmentDateTime());
            appointment.setStatus("SCHEDULED"); // Initial status is always SCHEDULED
            appointment.setNotes(dto.getNotes());
            appointment.setCreatedAt(LocalDateTime.now());

            // Update remaining appointments in package
            purchasedPackage.setRemainingAppointments(purchasedPackage.getRemainingAppointments() - 1);
            purchasedPackageRepository.save(purchasedPackage);

            // Save and return appointment
            return appointmentRepository.save(appointment);

        } catch (DataIntegrityViolationException e) {
            throw new AppointmentServiceException("Failed to schedule appointment due to data integrity violation", e);
        } catch (AppointmentServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new AppointmentServiceException("Unexpected error while scheduling appointment", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByNutritionistAndDateRange(
            Integer nutritionistId,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        
        // Validate nutritionist exists
        if (!nutritionistRepository.existsById(nutritionistId)) {
            throw new AppointmentServiceException("Nutritionist not found");
        }

        // Validate date range
        if (startDate.isAfter(endDate)) {
            throw new AppointmentServiceException("Start date must be before end date");
        }

        return appointmentRepository
                .findByNutritionistIdAndAppointmentDateTimeBetween(nutritionistId, startDate, endDate);
    }

    @Transactional
    public Appointment updateAppointmentStatus(Integer appointmentId, String newStatus) {
        try {
            // Validate status
            if (!VALID_STATUSES.contains(newStatus)) {
                throw new AppointmentServiceException(
                        "Invalid status. Must be one of: " + String.join(", ", VALID_STATUSES));
            }

            // Get and validate appointment exists
            Appointment appointment = appointmentRepository
                    .findById(appointmentId)
                    .orElseThrow(() -> new AppointmentServiceException("Appointment not found"));

            // Validate status transition
            validateStatusTransition(appointment.getStatus(), newStatus);

            // Update status
            appointment.setStatus(newStatus);
            return appointmentRepository.save(appointment);

        } catch (DataIntegrityViolationException e) {
            throw new AppointmentServiceException("Failed to update appointment status", e);
        }
    }

    @Transactional
    public void cancelAppointment(Integer appointmentId) {
        try {
            // Get and validate appointment exists
            Appointment appointment = appointmentRepository
                    .findById(appointmentId)
                    .orElseThrow(() -> new AppointmentServiceException("Appointment not found"));

            // Validate if appointment can be cancelled
            if ("COMPLETED".equals(appointment.getStatus()) || 
                "CANCELLED".equals(appointment.getStatus())) {
                throw new AppointmentServiceException(
                        "Cannot cancel appointment with status: " + appointment.getStatus());
            }

            // Restore appointment to package
            PurchasedPackage purchasedPackage = appointment.getPurchasedPackage();
            purchasedPackage.setRemainingAppointments(purchasedPackage.getRemainingAppointments() + 1);
            purchasedPackageRepository.save(purchasedPackage);

            // Update appointment status
            appointment.setStatus("CANCELLED");
            appointmentRepository.save(appointment);

        } catch (DataIntegrityViolationException e) {
            throw new AppointmentServiceException("Failed to cancel appointment", e);
        }
    }

    private void validateStatusTransition(String currentStatus, String newStatus) {
        // Prevent updating completed or cancelled appointments
        if ("COMPLETED".equals(currentStatus) || "CANCELLED".equals(currentStatus)) {
            throw new AppointmentServiceException(
                    "Cannot update status of " + currentStatus + " appointment");
        }

        // Add any other status transition rules here
        // For example, you might want to prevent transitioning from "NO_SHOW" to "SCHEDULED"
        if ("NO_SHOW".equals(currentStatus) && "SCHEDULED".equals(newStatus)) {
            throw new AppointmentServiceException(
                    "Cannot change status from NO_SHOW to SCHEDULED");
        }
    }

    @Transactional(readOnly = true)
    public Appointment getAppointmentById(Integer id) {
        return appointmentRepository
                .findById(id)
                .orElseThrow(() -> new AppointmentServiceException("Appointment not found"));
    }

    @Transactional(readOnly = true)
    public List<Appointment> getUpcomingAppointments(Integer nutritionistId) {
        if (!nutritionistRepository.existsById(nutritionistId)) {
            throw new AppointmentServiceException("Nutritionist not found");
        }

        LocalDateTime now = LocalDateTime.now();
        return appointmentRepository
                .findByNutritionistIdAndAppointmentDateTimeBetween(
                    nutritionistId, 
                    now, 
                    now.plusMonths(1)
                );
    }
}