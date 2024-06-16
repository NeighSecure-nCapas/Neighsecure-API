package com.example.neighsecureapi.services;

import com.example.neighsecureapi.domain.dtos.userDTOs.GoogleLoginDTO;
import com.example.neighsecureapi.domain.dtos.userDTOs.GoogleTokenInfo;
import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<String> exchangeCodeForAccessToken(String code, boolean isMobile);

    Mono<GoogleLoginDTO> fetchGoogleProfile(String accessToken);
}
