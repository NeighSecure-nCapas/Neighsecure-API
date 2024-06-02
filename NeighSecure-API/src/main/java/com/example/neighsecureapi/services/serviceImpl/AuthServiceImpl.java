package com.example.neighsecureapi.services.serviceImpl;

import com.example.neighsecureapi.domain.dtos.GeneralResponse;
import com.example.neighsecureapi.domain.dtos.userDTOs.GoogleLoginDTO;
import com.example.neighsecureapi.domain.dtos.userDTOs.GoogleTokenInfo;
import com.example.neighsecureapi.services.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @Override
    public Mono<String> exchangeCodeForAccessToken(String code) {

        log.info("Exchanging code for access token... {}", code);
        log.info("Redirect URI... {}", redirectUri);
        WebClient webClient = WebClient.create();

        String googleUri = "https://oauth2.googleapis.com/token";
        String authorizationCode = code.replace("\"", "");

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        formData.set("code", authorizationCode);
        formData.set("client_id", clientId);
        formData.set("client_secret", clientSecret);
        formData.set("redirect_uri", redirectUri);
        formData.set("grant_type", "authorization_code");

        log.info("Form data: {}", formData);

        return webClient.post()
                .uri(googleUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(GoogleTokenInfo.class)
                .flatMap(tokenInfo -> {
                    log.info("Token info: {}", tokenInfo);
                    return Mono.just(tokenInfo.getAccess_token());
                })
                .onErrorResume(e -> {
                    log.error("Error fetching user access token", e);
                    return Mono.error(e);
                });
    }

    @Override
    public Mono<GoogleLoginDTO> fetchGoogleProfile(String accessToken) {

        log.info("Fetching user info... {}", accessToken);

        WebClient webClient = WebClient.create();
        String googleUri = "https://www.googleapis.com/oauth2/v3/userinfo?access_token=" + accessToken;

        return webClient.get()
                .uri(googleUri)
                .retrieve()
                .bodyToMono(GoogleLoginDTO.class)
                .doOnNext(UserInfo -> log.info("UserInfo info: {}", UserInfo))
                .onErrorResume(e -> {
                    log.error("Error fetching user info", e);
                    return Mono.error(e);
                });
    }
}
