package com.example.neighsecureapi.domain.dtos.userDTOs;

import lombok.Data;

import java.util.UUID;

@Data
public class RoleUpdateDTO {
    private UUID userId;
    private String role;
}
