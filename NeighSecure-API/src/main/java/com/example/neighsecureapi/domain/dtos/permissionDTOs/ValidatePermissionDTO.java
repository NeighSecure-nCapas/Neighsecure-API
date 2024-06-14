package com.example.neighsecureapi.domain.dtos.permissionDTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.UUID;

@Data
public class ValidatePermissionDTO {
    // campos de la llave
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate generationDate;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime generationTime;

    @NotBlank
    private String generationDay;

    // campos del rol
    //@NotBlank
    //private String role;

    // campo del permiso
    private UUID permissionId;

}
