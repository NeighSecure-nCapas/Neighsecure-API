package com.example.neighsecureapi.services.serviceImpl;


import com.example.neighsecureapi.domain.entities.Terminal;
import com.example.neighsecureapi.repositories.TerminalRepository;
import com.example.neighsecureapi.services.TerminalService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TerminalServiceImplementation implements TerminalService {

    private final TerminalRepository terminalRepository;

    public TerminalServiceImplementation(TerminalRepository terminalRepository) {
        this.terminalRepository = terminalRepository;
    }

    @Override
    public List<Terminal> getAllTerminals() {
        return terminalRepository.findAll();
    }

    @Override
    public Terminal getTerminalById(UUID terminalId) {
        return terminalRepository.findById(terminalId).orElse(null);
    }
}
