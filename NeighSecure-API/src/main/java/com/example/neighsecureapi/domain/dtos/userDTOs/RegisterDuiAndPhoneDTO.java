package com.example.neighsecureapi.domain.dtos.userDTOs;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterDuiAndPhoneDTO {

    @Pattern(regexp = "^[0-9]{8}-[0-9]$", message = "DUI must be in the format 00000000-0")
    private String dui;

    @Pattern(regexp = "^[0-9]{8}$", message = "Phone must be 8 digits")
    private String phone;
}
