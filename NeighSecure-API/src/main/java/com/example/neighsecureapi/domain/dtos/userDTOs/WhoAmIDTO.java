package com.example.neighsecureapi.domain.dtos.userDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WhoAmIDTO {
    @NotNull
    private String token;
}
