package com.example.neighsecureapi.services;

import com.example.neighsecureapi.domain.dtos.entryDTOs.EntryRegisterDTO;
import com.example.neighsecureapi.domain.entities.Entry;
import com.example.neighsecureapi.domain.entities.Permission;
import com.example.neighsecureapi.domain.entities.Terminal;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface EntryService {

    // CRUD IMPLEMENTATION FOR ENTRY ENTITY
    public void saveEntry(EntryRegisterDTO info, Terminal terminal, Permission permission);
    public void deleteEntry(UUID entryId);
    // public void updateEntry(String entryName, String entryAddress);
    public Entry getEntry(UUID entryId);

    public List<Entry> getAllEntries();

    public List<Entry> getAllEntriesByDate(Date date);

    // END OF CRUD IMPLEMENTATION ---------------------------------------------------------------------

    // ADDITIONAL METHODS
    // public void addKeyToEntry(String entryId, String key);

    // recibir el numero de visitas en un dia
    // public Integer getEntriesByDate(String date);

    // numero de visitantes por hora en un dia
    // public Integer getEntriesByHour(String date, String hour);

}
