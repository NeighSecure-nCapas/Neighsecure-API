package com.example.neighsecureapi.domain.dtos.homeDTOs;

import com.example.neighsecureapi.domain.dtos.userDTOs.PresentationUserDetailsDTO;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PresentationHomeDetailDTO {
    private UUID id;

    private Integer homeNumber;

    private Integer membersNumber;

    private PresentationUserDetailsDTO homeBoss;

    private List<PresentationUserDetailsDTO> members;
}
