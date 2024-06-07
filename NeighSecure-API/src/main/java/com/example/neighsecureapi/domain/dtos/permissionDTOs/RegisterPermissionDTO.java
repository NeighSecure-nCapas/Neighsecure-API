package com.example.neighsecureapi.domain.dtos.permissionDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.UUID;

@Data
public class RegisterPermissionDTO {
    @NotBlank
    private String type;

    @NotBlank
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date startDate;

    @NotBlank
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date endDate;

    @NotBlank
    @DateTimeFormat(pattern = "HH:mm")
    private Date startTime;

    @NotBlank
    @DateTimeFormat(pattern = "HH:mm")
    private Date endTime;

    @NotBlank
    private String days;

    @NotNull
    private UUID homeId;

    @NotNull
    private UUID visitor;// quien recibe el permiso

    @NotNull
    private UUID grantedBy;// quien da el permiso
}
