package com.example.neighsecureapi.services;

import com.example.neighsecureapi.domain.entities.Token;

public interface TokenService {
    public Token findTokenBycontent(String content);
}
