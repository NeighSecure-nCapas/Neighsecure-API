package com.example.neighsecureapi.services.serviceImpl;

import com.example.neighsecureapi.domain.entities.Token;
import com.example.neighsecureapi.repositories.TokenRepository;
import com.example.neighsecureapi.services.TokenService;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImplementation implements TokenService {

    private final TokenRepository tokenRepository;

    public TokenServiceImplementation(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public Token findTokenBycontent(String content) {
        return tokenRepository.findByContent(content).orElse(null);
    }
}
