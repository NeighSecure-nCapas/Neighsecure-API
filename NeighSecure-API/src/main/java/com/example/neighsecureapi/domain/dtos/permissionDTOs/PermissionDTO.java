package com.example.neighsecureapi.domain.dtos.permissionDTOs;

import lombok.Data;

import java.util.Date;

@Data
public class PermissionDTO {
    private String type;
    private Date startDate;
    private Date endDate;
    private Date startTime;
    private Date endTime;
    private Boolean status;
    private boolean valid;
    private Date generationDate;
    // ya recibe el arreglo de dias separado por comas ","
    private String days;
}
