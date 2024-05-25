package com.example.neighsecureapi.services.serviceImpl;


import com.example.neighsecureapi.domain.dtos.userDTOs.RegisterUserDTO;
import com.example.neighsecureapi.domain.entities.Home;
import com.example.neighsecureapi.domain.entities.Role;
import com.example.neighsecureapi.domain.entities.User;
import com.example.neighsecureapi.repositories.UserRepository;
import com.example.neighsecureapi.services.UserService;
import jakarta.transaction.Transactional;
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
    @Transactional(rollbackOn = Exception.class)
    public void saveUser(RegisterUserDTO info, Role rol) {
        User user = new User();

        user.setName(info.getName());
        user.setEmail(info.getEmail());
        user.setDui(info.getDui());
        user.setHomeId(null);
        user.setPhone(info.getPhone());
        user.setRolId(List.of(rol));
        user.setStatus(true);

        userRepository.save(user);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
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
        // TODO: implementar paginacion
        return userRepository.findAllByStatusTrue();
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void addRoleToUser(User user, Role role) {
        user.getRolId().add(role);
        userRepository.save(user);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void updateRoleToUser(User user, Role role) {

        // setea el rol como una lista de un nuevo rol
        user.setRolId(List.of(role));
        userRepository.save(user);

    }

    @Override
    public User findUserByEmail(String email) {

        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void addHomeToUser(User user, Home home) {
        user.setHomeId(home);
        userRepository.save(user);
    }

    @Override
    public User findUserByEmailAndDui(String email, String dui) {
        return userRepository.findByEmailAndAndDui(email, dui).orElse(null);
    }

}
