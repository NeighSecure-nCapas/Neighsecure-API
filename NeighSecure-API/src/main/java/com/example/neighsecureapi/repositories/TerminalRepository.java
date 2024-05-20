package com.example.neighsecureapi.repositories;

import com.example.neighsecureapi.domain.entities.Terminal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TerminalRepository extends JpaRepository<Terminal, UUID> {
}
