package com.example.neighsecureapi.domain.dtos.entryDTOs;

import com.example.neighsecureapi.domain.dtos.homeDTOs.PresentationHomeDTO;
import com.example.neighsecureapi.domain.dtos.userDTOs.PresentationUserDetailsDTO;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class PresentationEntryDetailsDTO {

    private UUID id;

    private PresentationUserDetailsDTO user;

    private Date date;

    private PresentationHomeDTO home;

    private String entryType; // normal o anonima
}
