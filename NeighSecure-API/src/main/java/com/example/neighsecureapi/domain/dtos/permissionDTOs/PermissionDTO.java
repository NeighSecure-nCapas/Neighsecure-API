package com.example.neighsecureapi.domain.dtos.permissionDTOs;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Data
public class PermissionDTO {
    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean status;
    private boolean valid;
    private Date generationDate;
    // ya recibe el arreglo de dias separado por comas ","
    private String days;
}
