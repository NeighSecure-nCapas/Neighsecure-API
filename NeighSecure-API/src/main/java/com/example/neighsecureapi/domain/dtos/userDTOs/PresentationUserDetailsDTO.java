package com.example.neighsecureapi.domain.dtos.userDTOs;

import com.example.neighsecureapi.domain.entities.Role;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PresentationUserDetailsDTO {

    private UUID id;

    private String name;

    private String email;

    private String dui;

    private String phoneNumber;

    private List<Role> roles;
}
