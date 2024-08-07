package com.example.neighsecureapi.services;

import com.example.neighsecureapi.domain.dtos.userDTOs.RegisterUserDTO;
import com.example.neighsecureapi.domain.entities.Home;
import com.example.neighsecureapi.domain.entities.Role;
import com.example.neighsecureapi.domain.entities.Token;
import com.example.neighsecureapi.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface UserService {

    // CRUD IMPLEMENTATION FOR USER ENTITY
    public void saveUser(RegisterUserDTO info, Role role);
    public void deleteUser(UUID userId);

    public void updateUser(String username, String password);
    public User getUser(UUID userId);

    // TODO: implementar pagination
    public List<User> getAllUsers();
    public Page<User> getAllUsersByRole(Pageable pageable, Role rol);

    // END OF CRUD IMPLEMENTATION

    // ADDITIONAL METHODS
    public void addRoleToUser(User user, Role role);
    public void updateRoleToUser(User user, Role role);
    public  void deleteRoleToUser(User user, Role role);
    public User findUserByEmail(String email);
    //public void addHomeToUser(User user, Home home);
    public User findUserByEmailAndDui(String email, String dui);
    public User findUserByName(String name);
    public User findByIdentifier(String identifier);
    public void setDuiAndPhoneToUser(User user, String dui, String phone);
    public User findUserByToken(Token token);
    public List<User> getAllUsersByRole(Role rol);
    //public void updateHomeToUser(User user, Home home);

    // Integer getUsersNumberByRole(String date, String roleId);
    // public void removeRoleFromUser(String userId, String roleId);
    // public List<User> getUsersByRole(String roleId);

    // obtener el numero de usuarios por rol
    // public Integer getUsersNumberByRole(String date, String roleId);

    // jwt ------------------------------------------------

    //Token management
    Token registerToken(User user) throws Exception;
    Boolean isTokenValid(User user, String token);
    void cleanTokens(User user) throws Exception;

    //Find User authenticated
    User findUserAuthenticated();

}
