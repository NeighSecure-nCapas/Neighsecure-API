package com.example.neighsecureapi.repositories;

import com.example.neighsecureapi.domain.entities.Token;
import com.example.neighsecureapi.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, UUID> {

    List<Token> findByUserAndActive(User user, Boolean active);
    Optional<Token> findByContent(String content);

}
