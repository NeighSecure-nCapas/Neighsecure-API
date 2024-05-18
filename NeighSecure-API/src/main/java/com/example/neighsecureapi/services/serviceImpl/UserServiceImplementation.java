package com.example.neighsecureapi.services.serviceImpl;


import com.example.neighsecureapi.domain.dtos.RegisterUserDTO;
import com.example.neighsecureapi.domain.entities.Home;
import com.example.neighsecureapi.domain.entities.Role;
import com.example.neighsecureapi.domain.entities.User;
import com.example.neighsecureapi.repositories.UserRepository;
import com.example.neighsecureapi.services.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;

    public UserServiceImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void saveUser(RegisterUserDTO info, Home casa) {
        User user = new User();

        user.setName(info.getName());
        user.setEmail(info.getEmail());
        user.setDui(info.getDui());
        user.setHomeId(casa);
        user.setStatus(true);

        userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID userId) {
        // no lo elimina de manera literal, solo lo desactiva
        User user = userRepository.findById(userId).orElse(null);

        if (user != null) {
            user.setStatus(false);
            userRepository.save(user);
        }
    }

    @Override
    public void updateUser(String username, String password) {

    }

    @Override
    public User getUser(UUID userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        // TODO: implementar paginacion y buscar solo los activos
        return userRepository.findAll();
    }

    @Override
    public void updateRoleToUser(UUID userId, Role role) {
        User user = userRepository.findById(userId).orElse(null);

        if (user != null) {
            user.getRolId().add(role);
            userRepository.save(user);
        }

    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
