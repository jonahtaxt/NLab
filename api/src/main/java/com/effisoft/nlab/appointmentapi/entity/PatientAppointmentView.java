package com.effisoft.nlab.appointmentapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "v_patient_appointments", schema = "nlab")
public class PatientAppointmentView {
    @Id
    @Column(name = "appointment_id")
    private Integer appointmentId;

    @Column(name = "patient_id")
    private Integer patientId;

    @Column(name = "nutritionist_id")
    private Integer nutritionistId;

    @Column(name = "nutritionist_name")
    private String nutritionistName;

    @Column(name = "package_name")
    private String packageName;

    @Column(name = "appointment_date")
    private LocalDate appointmentDate;

    @Column(name = "appointment_time")
    private LocalTime appointmentTime;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}