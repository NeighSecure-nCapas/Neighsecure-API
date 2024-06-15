package com.example.neighsecureapi.domain.dtos.homeDTOs;

import lombok.Data;

import java.util.UUID;

@Data
public class PresentationHomeDTO {
    private UUID id;

    private Integer homeNumber;

    private String HomeBoss;
}
