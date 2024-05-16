package com.example.neighsecureapi.services.serviceImpl;


import com.example.neighsecureapi.domain.entities.User;
import com.example.neighsecureapi.services.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImplementation implements UserService {
    @Override
    public void saveUser(String username, String password) {

    }

    @Override
    public void deleteUser(String userId) {

    }

    @Override
    public void updateUser(String username, String password) {

    }

    @Override
    public User getUser(String username) {
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return List.of();
    }

    @Override
    public void addRoleToUser(String userId, String roleId) {

    }

    @Override
    public void removeRoleFromUser(String userId, String roleId) {

    }

    @Override
    public List<User> getUsersByRole(String roleId) {
        return List.of();
    }

    @Override
    public Integer getUsersNumberByRole(String date, String roleId) {
        return 0;
    }
}
