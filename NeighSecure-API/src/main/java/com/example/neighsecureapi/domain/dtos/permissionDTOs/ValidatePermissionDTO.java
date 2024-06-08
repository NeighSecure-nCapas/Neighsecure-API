package com.example.neighsecureapi.domain.dtos.permissionDTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.UUID;

@Data
public class ValidatePermissionDTO {
    // campos de la llave
    @NotBlank
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date generationDate;

    @NotBlank
    @DateTimeFormat(pattern = "HH:mm")
    private Date generationTime;

    @NotBlank
    private String generationDay;

    // campos del rol
    @NotBlank
    private String role;

    // campo del permiso
    private UUID permissionId;

}
