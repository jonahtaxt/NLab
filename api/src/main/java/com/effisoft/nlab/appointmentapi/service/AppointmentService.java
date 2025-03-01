package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.dto.AppointmentDTO;
import com.effisoft.nlab.appointmentapi.entity.Appointment;
import com.effisoft.nlab.appointmentapi.entity.Nutritionist;
import com.effisoft.nlab.appointmentapi.entity.PurchasedPackage;
import com.effisoft.nlab.appointmentapi.exception.AppointmentServiceException;
import com.effisoft.nlab.appointmentapi.repository.AppointmentRepository;
import com.effisoft.nlab.appointmentapi.repository.NutritionistRepository;
import com.effisoft.nlab.appointmentapi.repository.PurchasedPackageRepository;
import com.effisoft.nlab.appointmentapi.service.base.ServiceExceptionHandler;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
        return ServiceExceptionHandler.executeWithExceptionHandling(
            () -> {
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
            },
            AppointmentServiceException::new,
            "Schedule Appointment"
        );
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByNutritionistAndDateRange(
            Integer nutritionistId,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
            () -> {
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
            },
            AppointmentServiceException::new,
            "Get Appointments By Nutritionist And Date Range"
        );
        
    }

    @Transactional
    public Appointment updateAppointmentStatus(Integer appointmentId, String newStatus) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
            () -> {
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
            },
            AppointmentServiceException::new,
            "Update Appointment Status"
        );
    }

    @Transactional
    public Appointment cancelAppointment(Integer appointmentId) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
            () -> {
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
                return appointmentRepository.save(appointment);

            },
            AppointmentServiceException::new,
            "Cancel Appointment"
        );
    }

    @Transactional(readOnly = true)
    public Appointment getAppointmentById(Integer id) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
            () -> appointmentRepository
                    .findById(id)
                    .orElseThrow(() -> new AppointmentServiceException("Appointment not found")),
            AppointmentServiceException::new,
            "Get Appointment By Id"
        );
    }

    @Transactional(readOnly = true)
    public List<Appointment> getUpcomingAppointments(Integer nutritionistId) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
            () -> {
                // Validate nutritionist exists
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
            },
            AppointmentServiceException::new,
            "Get Upcoming Appointments"
        );
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
}