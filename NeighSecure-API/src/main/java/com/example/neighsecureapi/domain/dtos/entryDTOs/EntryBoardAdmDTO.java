package com.example.neighsecureapi.domain.dtos.entryDTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class EntryBoardAdmDTO {
    @NotBlank
    private UUID id;

    @NotBlank
    private Date date;

    @NotBlank
    private String user;

    @NotBlank
    private Integer homeNumber;

    @NotBlank
    private String entryTypeTerminal;

    private String entryType;
}
