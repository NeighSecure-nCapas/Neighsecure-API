package com.example.neighsecureapi.domain.dtos.homeDTOs;

import com.example.neighsecureapi.domain.entities.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class HomeFullDataDTO {
    @NotBlank
    private UUID id;

    @NotBlank
    private Integer homeNumber;

    @NotBlank
    private String address;

    @NotBlank
    private Integer membersNumber;

    @NotBlank
    private User userAdmin;

    @NotBlank
    private List<User> homeMembers;

}
