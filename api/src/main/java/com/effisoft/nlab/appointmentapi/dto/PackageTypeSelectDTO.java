package com.effisoft.nlab.appointmentapi.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PackageTypeSelectDTO {
    private Integer id;
    private String name;
    private Integer numberOfAppointments;
    private BigDecimal price;
}
