package com.example.neighsecureapi.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Data
public class KeyUpdateDTO {
    @NotBlank
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate generationDate;

    @NotBlank
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime generationTime;

    @NotBlank
    private String generationDay;
}
