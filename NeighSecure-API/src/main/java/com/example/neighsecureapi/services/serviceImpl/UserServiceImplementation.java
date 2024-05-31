package com.example.neighsecureapi.services.serviceImpl;


import com.example.neighsecureapi.domain.dtos.userDTOs.RegisterUserDTO;
import com.example.neighsecureapi.domain.entities.Role;
import com.example.neighsecureapi.domain.entities.Token;
import com.example.neighsecureapi.domain.entities.User;
import com.example.neighsecureapi.repositories.TokenRepository;
import com.example.neighsecureapi.repositories.UserRepository;
import com.example.neighsecureapi.services.UserService;
import com.example.neighsecureapi.utils.jwt.JWTTools;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private final JWTTools jwtTools;
    private final TokenRepository tokenRepository;

    public UserServiceImplementation(UserRepository userRepository, JWTTools jwtTools, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.jwtTools = jwtTools;
        this.tokenRepository = tokenRepository;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void saveUser(RegisterUserDTO info, Role rol) {
        User user = new User();

        user.setName(info.getName());
        user.setEmail(info.getEmail());
        user.setDui(info.getDui());
        //user.setHomeId(null);
        user.setPhone(info.getPhone());
        user.setRolId(List.of(rol));
        user.setActive(true);

        userRepository.save(user);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void deleteUser(UUID userId) {
        // no lo elimina de manera literal, solo lo desactiva
        User user = userRepository.findById(userId).orElse(null);

        if (user != null) {
            user.setActive(false);
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
        return userRepository.findAllByActiveIsTrue();
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
        user.setRolId(new ArrayList<>());

        // agrega el elemento a la lista
        user.getRolId().add(role);
        userRepository.save(user);

    }

    @Override
    public User findUserByEmail(String email) {

        return userRepository.findByEmailAndActiveIsTrue(email).orElse(null);
    }

    /*
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void addHomeToUser(User user, Home home) {
        user.setHomeId(home);
        userRepository.save(user);
    }
    * */

    @Override
    public User findUserByEmailAndDui(String email, String dui) {
        return userRepository.findByEmailAndDui(email, dui).orElse(null);
    }

    @Override
    public User findUserByName(String name) {
        return userRepository.findUserByNameAndActiveIsTrue(name).orElse(null);
    }

    @Override
    public User findByIdentifier(String identifier) {
        return userRepository.findByEmailOrDuiOrNameAndActiveIsTrue(identifier, identifier, identifier).orElse(null);
    }

    // TOKEN -----------------------------------------------------------------------

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Token registerToken(User user) throws Exception {
        cleanTokens(user);

        String tokenString = jwtTools.generateToken(user);
        Token token = new Token(tokenString, user);

        tokenRepository.save(token);

        return token;
    }

    @Override
    public Boolean isTokenValid(User user, String token) {
        try {
            cleanTokens(user);
            List<Token> tokens = tokenRepository.findByUserAndActive(user, true);

            tokens.stream()
                    .filter(tk -> tk.getContent().equals(token))
                    .findAny()
                    .orElseThrow(() -> new Exception());

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void cleanTokens(User user) throws Exception {
        List<Token> tokens = tokenRepository.findByUserAndActive(user, true);

        tokens.forEach(token -> {
            if(!jwtTools.verifyToken(token.getContent())) {
                token.setActive(false);
                tokenRepository.save(token);
            }
        });
    }

    @Override
    public User findUserAuthenticated() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findUserByNameAndActiveIsTrue(username).orElse(null);
    }

    /*
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void updateHomeToUser(User user, Home home) {
        user.setHomeId(home);
        userRepository.save(user);
    }
    * */

}
