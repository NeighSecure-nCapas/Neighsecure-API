package com.example.neighsecureapi.domain.dtos.userDTOs;

import com.example.neighsecureapi.domain.entities.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class WhoAmIDTO {

    private UUID userId;

    private String username;

    private String email;

    private List<Role> roles;

    private String phoneNumber;

    private String dui;

    private UUID homeId;
}
