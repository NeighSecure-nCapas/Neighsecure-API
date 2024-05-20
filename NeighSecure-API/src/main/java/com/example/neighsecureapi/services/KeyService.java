package com.example.neighsecureapi.services;

import com.example.neighsecureapi.domain.dtos.KeyUpdateDTO;
import com.example.neighsecureapi.domain.entities.Key;

import java.util.List;
import java.util.UUID;

public interface KeyService {
    // CRUD IMPLEMENTATION FOR KEY ENTITY
    // TODO: aplicar dtos
    public void saveKey();
    public void deleteKey(String keyId);

    // hay q validar la vigencia del permiso
    public void updateKey(Key key, KeyUpdateDTO info);
    public Key getKey(UUID keyId);
    public List<Key> getAllKeys();

    // END OF CRUD IMPLEMENTATION ---------------------------------------------------------------------

    // ADDITIONAL METHODS

}
