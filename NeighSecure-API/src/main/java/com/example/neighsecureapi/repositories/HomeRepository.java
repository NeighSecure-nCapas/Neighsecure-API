package com.example.neighsecureapi.repositories;

import com.example.neighsecureapi.domain.entities.Home;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HomeRepository extends JpaRepository<Home, UUID> {
}
