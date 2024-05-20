package com.example.neighsecureapi.services;

import com.example.neighsecureapi.domain.entities.Terminal;

import java.util.List;
import java.util.UUID;

public interface TerminalService {
    // no hay crud por q es tabla catalogo y no se va a modificar, o no desde interfaz

    public List<Terminal> getAllTerminals();
    public Terminal getTerminalById(UUID terminalId);
}
