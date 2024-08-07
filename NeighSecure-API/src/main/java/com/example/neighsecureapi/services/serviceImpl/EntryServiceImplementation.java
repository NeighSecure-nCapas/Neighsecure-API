package com.example.neighsecureapi.services.serviceImpl;

import com.example.neighsecureapi.domain.dtos.entryDTOs.EntryRegisterDTO;
import com.example.neighsecureapi.domain.entities.Entry;
import com.example.neighsecureapi.domain.entities.Permission;
import com.example.neighsecureapi.domain.entities.Terminal;
import com.example.neighsecureapi.repositories.EntryRepository;
import com.example.neighsecureapi.services.EntryService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class EntryServiceImplementation implements EntryService {

    private final EntryRepository entryRepository;

    public EntryServiceImplementation(EntryRepository entryRepository) {
        this.entryRepository = entryRepository;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void saveEntry(EntryRegisterDTO info, Terminal terminal, Permission permission) {
        Entry entry = new Entry();

        entry.setEntryDate(info.getDateAndHour());
        entry.setTerminalId(terminal);
        entry.setPermissionId(permission);
        entry.setComment(info.getComment());

        entryRepository.save(entry);

    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void deleteEntry(UUID entryId) {
        entryRepository.deleteById(entryId);
    }

    @Override
    public Entry getEntry(UUID entryId) {
        return entryRepository.findById(entryId).orElse(null);
    }

    @Override
    public List<Entry> getAllEntries() {
        return entryRepository.findAll();
    }

    @Override
    public Page<Entry> getAllEntries(Pageable pageable) {
        return entryRepository.findAll(pageable);
    }

    @Override
    public List<Entry> getAllEntriesByDate(LocalDate date) {

        //return entryRepository.findAllByEntryDate(date);
        return entryRepository.findAllByEntryDateIgnoringTime(date);
    }


}
