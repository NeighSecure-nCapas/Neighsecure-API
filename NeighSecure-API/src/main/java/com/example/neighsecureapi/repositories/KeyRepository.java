package com.example.neighsecureapi.repositories;

import com.example.neighsecureapi.domain.entities.Key;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface KeyRepository extends JpaRepository<Key, UUID> {
}
