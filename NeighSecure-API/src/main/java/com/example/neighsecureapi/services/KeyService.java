package com.example.neighsecureapi.services;

import com.example.neighsecureapi.domain.entities.Key;

import java.util.List;

public interface KeyService {
    // CRUD IMPLEMENTATION FOR KEY ENTITY
    // TODO: aplicar dtos
    public void saveKey(String keyName, String keyAddress);
    public void deleteKey(String keyId);

    // hay q validar la vigencia del permiso
    public void updateKey(String keyName, String keyAddress);
    public Key getKey(String keyName);
    public List<Key> getAllKeys();

    // END OF CRUD IMPLEMENTATION ---------------------------------------------------------------------

    // ADDITIONAL METHODS

}
