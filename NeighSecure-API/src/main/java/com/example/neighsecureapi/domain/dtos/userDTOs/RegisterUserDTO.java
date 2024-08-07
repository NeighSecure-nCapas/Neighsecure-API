package com.example.neighsecureapi.domain.dtos.userDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterUserDTO {
    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String dui;

    @NotBlank
    private String phone;
}
