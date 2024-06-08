package com.example.neighsecureapi.services.serviceImpl;

import com.example.neighsecureapi.domain.dtos.KeyUpdateDTO;
import com.example.neighsecureapi.domain.entities.Key;
import com.example.neighsecureapi.repositories.KeyRepository;
import com.example.neighsecureapi.services.KeyService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
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
    public void saveKey(Key key) {
        // esta vacio ya que es la primera generacion de la llave, se inicia con valores null
        // quiza no sea ni necesaria la funcion, directamente se crea una llave en el controller

        key.setGenerationDate(null);
        key.setGenerationTime(null);
        key.setGenerationDay(null);

        keyRepository.save(key);

    }

    @Override
    public void deleteKey(Key keyId) {
        keyRepository.delete(keyId);
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

    @Override
    public boolean keyIsStillValid(Key key) {
        // valida si la hora y fecha actual es antes que 10 minutos de la hora y fecha de generacion de la llave

        return key.getGenerationTime().after(Date.from(Instant.now().minus(10, ChronoUnit.MINUTES) ));

        /*
        funcion de copilot
        // Obtén la hora actual
        Date currentTime = new Date();

        // Calcula la diferencia en minutos
        long diffInMillies = Math.abs(currentTime.getTime() - key.getGenerationTime().getTime());
        long diffInMinutes = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);

        // Si la diferencia es menor o igual a 10, la hora es válida
        return diffInMinutes <= 10;
        * */

    }
}
