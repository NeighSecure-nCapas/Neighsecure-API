package com.example.neighsecureapi.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HomeRegisterDTO {
    @NotBlank
    private Integer homeNumber;

    @NotBlank
    private String address;

    @NotBlank
    private Integer membersNumber;
}
