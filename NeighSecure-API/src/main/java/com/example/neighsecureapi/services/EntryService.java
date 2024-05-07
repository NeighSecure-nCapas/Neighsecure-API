package com.example.neighsecureapi.services;

import com.example.neighsecureapi.domain.entities.Entry;

import java.util.List;

public interface EntryService {

    // CRUD IMPLEMENTATION FOR ENTRY ENTITY
    // TODO: aplicar dtos
    public void saveEntry(String entryName, String entryAddress);
    public void deleteEntry(String entryId);
    public void updateEntry(String entryName, String entryAddress);
    public Entry getEntry(String entryName);

    public List<Entry> getAllEntries();

    // END OF CRUD IMPLEMENTATION ---------------------------------------------------------------------

    // ADDITIONAL METHODS
    public void addKeyToEntry(String entryId, String key);

    // recibir el numero de visitas en un dia
    public Integer getEntriesByDate(String date);

    // numero de visitantes por hora en un dia
    public Integer getEntriesByHour(String date, String hour);

}
