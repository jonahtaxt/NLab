package com.effisoft.nlab.appointmentapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "vPatientAppointments")
public class PatientAppointmentView {
    @Id
    @Column(name = "AppointmentId")
    private Integer appointmentId;

    @Column(name = "PatientId")
    private Integer patientId;

    @Column(name = "NutritionistId")
    private Integer nutritionistId;

    @Column(name = "NutritionistName")
    private String nutritionistName;

    @Column(name = "PackageName")
    private String packageName;

    @Column(name = "AppointmentDate")
    private LocalDate appointmentDate;

    @Column(name = "AppointmentTime")
    private LocalTime appointmentTime;

    @Column(name = "Status")
    private String status;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;
}