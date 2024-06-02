package com.example.neighsecureapi.domain.dtos.userDTOs;

import lombok.Data;

@Data
public class GoogleTokenInfo {
    private String access_token;
    private Number expires_in;
    private String scope;
    private String refresh_token;
}
