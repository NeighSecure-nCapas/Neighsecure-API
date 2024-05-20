package com.example.neighsecureapi.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class KeyUpdateDTO {
    @NotBlank
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date generationDate;

    @NotBlank
    @DateTimeFormat(pattern = "HH:mm")
    private Date generationTime;

    @NotBlank
    private String generationDay;
}
