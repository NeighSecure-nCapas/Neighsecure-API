package com.example.neighsecureapi.domain.dtos.entryDTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class EntryRegisterDTO {
    @NotBlank
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private Date dateAndHour;

    String comment;
}
