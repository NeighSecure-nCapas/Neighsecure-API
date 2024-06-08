package com.example.neighsecureapi.repositories;

import com.example.neighsecureapi.domain.entities.Token;
import com.example.neighsecureapi.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailAndActiveIsTrue(String email);
    List<User> findAllByActiveIsTrue();
    Optional<User> findByEmailAndDui(String email, String dui);
    Optional<User> findUserByNameAndActiveIsTrue(String name);
    Optional<User> findByEmailOrDuiOrNameAndActiveIsTrue(String email, String dui, String name);
    Optional<User> findByTokens(List<Token> tokens);
}
