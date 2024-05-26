package com.example.neighsecureapi.utils;

import com.example.neighsecureapi.domain.dtos.HomeRegisterDTO;
import com.example.neighsecureapi.domain.dtos.HomeRegisterDataDTO;
import com.example.neighsecureapi.domain.entities.Home;
import com.example.neighsecureapi.domain.entities.Role;
import com.example.neighsecureapi.domain.entities.User;
import com.example.neighsecureapi.services.UserService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class UserHomeTools {

    private final UserService userService;

    public UserHomeTools(UserService userService) {
        this.userService = userService;
    }

    public void updateRoleAndHome(User user, Role rolAdm, Role rolMember, Home home, List<User> homeMembers){

        // actualizar el rol del usuario individual
        userService.updateRoleToUser(user, rolAdm);
        //userService.updateHomeToUser(user, home);

        // cambiar el rol de los usuarios de la lista

        for(User member : homeMembers) {
            if (!member.equals(user)) { // Evita la recursión en el usuario administrador
                userService.updateRoleToUser(member, rolMember);
                //userService.updateHomeToUser(member, home);
            }
        }

    }

    public HomeRegisterDTO createHomeRegisterDTO(HomeRegisterDataDTO info){

        HomeRegisterDTO homeRegisterDTO = new HomeRegisterDTO();

        homeRegisterDTO.setHomeNumber(info.getHomeNumber());
        homeRegisterDTO.setAddress(info.getAddress());
        homeRegisterDTO.setMembersNumber(info.getMembersNumber());
        homeRegisterDTO.setUserAdmin(userService.getUser(info.getUserAdmin()));
        // inicializar la lista de miembros
        homeRegisterDTO.setHomeMembers(new ArrayList<>());
        for(UUID userId : info.getHomeMembers()) {
            homeRegisterDTO.getHomeMembers().add(userService.getUser(userId));
        }

        return homeRegisterDTO;
    }

}
