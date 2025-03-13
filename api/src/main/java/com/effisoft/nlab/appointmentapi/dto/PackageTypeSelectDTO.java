package com.effisoft.nlab.appointmentapi.dto;

import lombok.Data;

@Data
public class PackageTypeSelectDTO {
    private String id;
    private String name;
    private Integer numberOfAppointments;
}
