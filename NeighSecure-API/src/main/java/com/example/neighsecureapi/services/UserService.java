package com.example.neighsecureapi.services;

import com.example.neighsecureapi.domain.dtos.RegisterUserDTO;
import com.example.neighsecureapi.domain.entities.Home;
import com.example.neighsecureapi.domain.entities.Role;
import com.example.neighsecureapi.domain.entities.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    // CRUD IMPLEMENTATION FOR USER ENTITY
    //TODO: implementar login
    public void saveUser(RegisterUserDTO info, Home casa);
    public void deleteUser(UUID userId);

    public void updateUser(String username, String password);
    public User getUser(UUID userId);

    // TODO: implementar pagination
    public List<User> getAllUsers();

    // END OF CRUD IMPLEMENTATION

    // ADDITIONAL METHODS
    public void updateRoleToUser(UUID userId, Role role);

    // Integer getUsersNumberByRole(String date, String roleId);
    // public void removeRoleFromUser(String userId, String roleId);
    // public List<User> getUsersByRole(String roleId);

    // obtener el numero de usuarios por rol
    // public Integer getUsersNumberByRole(String date, String roleId);


}
