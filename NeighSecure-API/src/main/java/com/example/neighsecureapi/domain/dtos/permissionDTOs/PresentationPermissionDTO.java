package com.example.neighsecureapi.domain.dtos.permissionDTOs;

import com.example.neighsecureapi.domain.dtos.userDTOs.UserResponseDTO;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.UUID;

@Data
public class PresentationPermissionDTO {
    private UUID id;

    private String type;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private Date generationDate;

    private String days;

    private UUID homeId;

    private Integer homeNumber;

    private String address;

    private UserResponseDTO userAssociated;

    private UserResponseDTO userAuth;

    private Boolean status; // aprobado true, rechazado false, pendiente null

    private boolean valid; // aun es utilizable true, ya no false(y si no ha sido aprobado, no se puede usar)
}
