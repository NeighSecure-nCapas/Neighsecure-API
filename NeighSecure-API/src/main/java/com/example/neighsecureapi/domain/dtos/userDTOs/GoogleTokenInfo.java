package com.example.neighsecureapi.domain.dtos.userDTOs;

import lombok.Data;

@Data
public class GoogleTokenInfo {
    private String email;
    private String name;
    private String picture;
    private boolean email_verified;

}
