package com.example.neighsecureapi.repositories;

import com.example.neighsecureapi.domain.entities.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface EntryRepository extends JpaRepository<Entry, UUID>{
    List<Entry> findAllByEntryDate(Date entryDate);
    @Query("SELECT e FROM Entry e WHERE DATE(e.entryDate) = :date")
    List<Entry> findAllByEntryDateIgnoringTime(@Param("date") LocalDate date);
}
