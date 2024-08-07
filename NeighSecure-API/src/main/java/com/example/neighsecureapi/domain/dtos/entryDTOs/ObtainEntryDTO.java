package com.example.neighsecureapi.domain.dtos.entryDTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.UUID;

@Data
public class ObtainEntryDTO {
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    private Date dateAndHour;

    String comment;

    @NotNull
    private UUID terminalId;

    private UUID keyId;

    @NotNull
    @NotBlank
    private String role;
}
