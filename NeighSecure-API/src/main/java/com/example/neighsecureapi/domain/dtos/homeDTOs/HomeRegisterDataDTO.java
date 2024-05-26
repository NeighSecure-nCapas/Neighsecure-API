package com.example.neighsecureapi.domain.dtos.homeDTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class HomeRegisterDataDTO {
    @NotBlank
    private Integer homeNumber;

    @NotBlank
    private String address;

    @NotBlank
    private Integer membersNumber;

    private UUID userAdmin;

    private List<UUID> homeMembers;
}
