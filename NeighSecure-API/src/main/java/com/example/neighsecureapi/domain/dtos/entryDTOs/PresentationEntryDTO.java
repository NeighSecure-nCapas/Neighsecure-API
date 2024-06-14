package com.example.neighsecureapi.domain.dtos.entryDTOs;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class PresentationEntryDTO {
    private UUID id;

    private Date date;

    private String user;

    private String entryType; // normal o anonima

    private Integer homeNumber;
}
