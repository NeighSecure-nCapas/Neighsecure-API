package com.example.neighsecureapi.services.serviceImpl;

import com.example.neighsecureapi.domain.dtos.KeyUpdateDTO;
import com.example.neighsecureapi.domain.entities.Key;
import com.example.neighsecureapi.repositories.KeyRepository;
import com.example.neighsecureapi.services.KeyService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class KeyServiceImplementation implements KeyService {

    private final KeyRepository keyRepository;

    public KeyServiceImplementation(KeyRepository keyRepository) {
        this.keyRepository = keyRepository;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void saveKey() {
        // esta vacio ya que es la primera generacion de la llave, se inicia con valores null
        // quiza no sea ni necesaria la funcion, directamente se crea una llave en el controller
        Key key = new Key();
        key.setGenerationDate(null);
        key.setGenerationTime(null);
        key.setGenerationDay(null);

        keyRepository.save(key);

    }

    @Override
    public void deleteKey(String keyId) {

    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void updateKey(Key key, KeyUpdateDTO info) {
        key.setGenerationDate(info.getGenerationDate());
        key.setGenerationTime(info.getGenerationTime());
        key.setGenerationDay(info.getGenerationDay());

        keyRepository.save(key);
    }

    @Override
    public Key getKey(UUID keyId) {
        return keyRepository.findById(keyId).orElse(null);
    }

    @Override
    public List<Key> getAllKeys() {
        return keyRepository.findAll();
        // innecesario
    }
}
