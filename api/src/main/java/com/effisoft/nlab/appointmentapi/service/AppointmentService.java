package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.entity.Appointment;
import com.effisoft.nlab.appointmentapi.entity.PurchasedPackage;
import com.effisoft.nlab.appointmentapi.repository.AppointmentRepository;
import com.effisoft.nlab.appointmentapi.repository.PurchasedPackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PurchasedPackageRepository purchasedPackageRepository;

    @Transactional
    public Appointment scheduleAppointment(Appointment appointment) {
        // Validate purchased package has remaining appointments
        PurchasedPackage purchasedPackage = appointment.getPurchasedPackage();

        if (purchasedPackage.getRemainingAppointments() <= 0) {
            throw new RuntimeException("No remaining appointments in the package");
        }

        // Set default status and creation time
        appointment.setStatus("SCHEDULED");
        appointment.setCreatedAt(LocalDateTime.now());

        // Decrease remaining appointments
        purchasedPackage.setRemainingAppointments(purchasedPackage.getRemainingAppointments() - 1);
        purchasedPackageRepository.save(purchasedPackage);

        return appointmentRepository.save(appointment);
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByNutritionistAndDateRange(
            Integer nutritionistId, LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentRepository.findByNutritionistIdAndAppointmentDateTimeBetween(
                nutritionistId, startDate, endDate);
    }

    @Transactional
    public Appointment updateAppointmentStatus(Integer appointmentId, String newStatus) {
        return appointmentRepository.findById(appointmentId)
                .map(appointment -> {
                    appointment.setStatus(newStatus);
                    return appointmentRepository.save(appointment);
                })
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    @Transactional
    public void cancelAppointment(Integer appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Restore remaining appointment to the package
        PurchasedPackage purchasedPackage = appointment.getPurchasedPackage();
        purchasedPackage.setRemainingAppointments(purchasedPackage.getRemainingAppointments() + 1);
        purchasedPackageRepository.save(purchasedPackage);

        // Update appointment status
        appointment.setStatus("CANCELLED");
        appointmentRepository.save(appointment);
    }
}
