package com.effisoft.nlab.appointmentapi.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppointmentNotesDTO {
    private Integer id;

    @NotNull(message = "Appointment ID is required")
    private Integer appointmentId;

    @NotNull(message = "Weight is required")
    @Min(value = 0, message = "Weight must be at least 0")
    private BigDecimal weight;

    @NotNull(message = "Total Fat is required")
    @Min(value = 0, message = "Total Fat must be at least 0")
    private BigDecimal totalFat;

    @NotNull(message = "Upper Fat is required")
    @Min(value = 0, message = "Upper Fat must be at least 0")
    private BigDecimal upperFat;

    @NotNull(message = "Lower Fat is required")
    @Min(value = 0, message = "Lower Fat must be at least 0")
    private BigDecimal lowerFat;

    @NotNull(message = "Visceral Fat is required")
    @Min(value = 0, message = "Visceral Fat must be at least 0")
    private BigDecimal visceralFat;

    @NotNull(message = "Muscle Mass is required")
    @Min(value = 0, message = "Muscle Mass must be at least 0")
    private BigDecimal muscleMass;

    @NotNull(message = "Bone Mass is required")
    @Min(value = 0, message = "Bone Mass must be at least 0")
    private BigDecimal boneMass;

    @NotNull(message = "Metabolic Age is required")
    @Min(value = 0, message = "Metabolic Age must be at least 0")
    private Integer metabolicAge;

    @NotNull(message = "Skinfold Subscapular is required")
    @Min(value = 0, message = "Skinfold Subscapular must be at least 0")
    private BigDecimal skinfoldSubscapular;

    @NotNull(message = "Skinfold Triceps is required")
    @Min(value = 0, message = "Skinfold Triceps must be at least 0")
    private BigDecimal skinfoldTriceps;

    @NotNull(message = "Skinfold Biceps is required")
    @Min(value = 0, message = "Skinfold Biceps must be at least 0")
    private BigDecimal skinfoldBiceps;

    @NotNull(message = "Skinfold Iliac Crest is required")
    @Min(value = 0, message = "Skinfold Iliac Crest must be at least 0")
    private BigDecimal skinfoldIliacCrest;

    @NotNull(message = "Skinfold Suprailiac is required")
    @Min(value = 0, message = "Skinfold Suprailiac must be at least 0")
    private BigDecimal skinfoldSuprailiac;

    @NotNull(message = "Skinfold Abdominal is required")
    @Min(value = 0, message = "Skinfold Abdominal must be at least 0")
    private BigDecimal skinfoldAbdominal;

    @NotNull(message = "Circumference Mid Arm Relaxed is required")
    @Min(value = 0, message = "Circumference Mid Arm Relaxed must be at least 0")
    private BigDecimal circumferenceMidArmRelaxed;

    @NotNull(message = "Circumference Mid Arm Flexed is required")
    @Min(value = 0, message = "Circumference Mid Arm Flexed must be at least 0")
    private BigDecimal circumferenceMidArmFlexed;

    @NotNull(message = "Circumference Umbilical is required")
    @Min(value = 0, message = "Circumference Umbilical must be at least 0")
    private BigDecimal circumferenceUmbilical;

    @NotNull(message = "Circumference Waist is required")
    @Min(value = 0, message = "Circumference Waist must be at least 0")
    private BigDecimal circumferenceWaist;

    @NotNull(message = "Circumference Hip is required")
    @Min(value = 0, message = "Circumference Hip must be at least 0")
    private BigDecimal circumferenceHip;

    private String notes;
}
