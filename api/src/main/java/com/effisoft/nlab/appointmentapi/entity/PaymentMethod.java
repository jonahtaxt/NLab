package com.effisoft.nlab.appointmentapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Entity
@Table(name = "PaymentMethod")
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Last name is required")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s]{2,50}$", message = "Last name must be 2-50 characters long and contain only letters")
    @Column(nullable = false)
    private String name;

    private String description;
}
