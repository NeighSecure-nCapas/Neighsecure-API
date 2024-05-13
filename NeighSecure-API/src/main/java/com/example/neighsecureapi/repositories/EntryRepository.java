package com.example.neighsecureapi.repositories;

import com.example.neighsecureapi.domain.entities.Entry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EntryRepository extends JpaRepository<Entry, UUID>{
}
