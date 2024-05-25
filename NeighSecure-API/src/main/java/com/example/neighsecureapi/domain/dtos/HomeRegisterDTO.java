package com.example.neighsecureapi.domain.dtos;

import com.example.neighsecureapi.domain.entities.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class HomeRegisterDTO {
    @NotBlank
    private Integer homeNumber;

    @NotBlank
    private String address;

    @NotBlank
    private Integer membersNumber;

    private User userAdmin;

    private List<User> homeMembers;
}
