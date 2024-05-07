package com.example.neighsecureapi.services;

import com.example.neighsecureapi.domain.entities.User;

import java.util.List;

public interface UserService {

    // CRUD IMPLEMENTATION FOR USER ENTITY
    //TODO: implementar dto
    public void saveUser(String username, String password);
    public void deleteUser(String userId);

    //TODO: implementar dto
    public void updateUser(String username, String password);
    public User getUser(String username);

    // TODO: implementar pagination
    public List<User> getAllUsers();

    // END OF CRUD IMPLEMENTATION


}
