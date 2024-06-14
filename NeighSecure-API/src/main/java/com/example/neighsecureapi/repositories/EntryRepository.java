package com.example.neighsecureapi.repositories;

import com.example.neighsecureapi.domain.entities.Entry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface EntryRepository extends JpaRepository<Entry, UUID>{
    List<Entry> findAllByEntryDate(Date entryDate);
}
