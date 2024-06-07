package com.example.neighsecureapi.domain.dtos.homeDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AddMemberDTO {
    @NotNull
    private UUID homeId;

    @NotNull
    private String userEmail;
}
