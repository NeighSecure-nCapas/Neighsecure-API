package com.example.neighsecureapi.utils;

import com.example.neighsecureapi.domain.dtos.homeDTOs.HomeRegisterDTO;
import com.example.neighsecureapi.domain.dtos.homeDTOs.HomeRegisterDataDTO;
import com.example.neighsecureapi.domain.entities.Home;
import com.example.neighsecureapi.domain.entities.Role;
import com.example.neighsecureapi.domain.entities.User;
import com.example.neighsecureapi.services.HomeService;
import com.example.neighsecureapi.services.RoleService;
import com.example.neighsecureapi.services.UserService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class UserHomeTools {

    private final UserService userService;
    private final HomeService homeService;
    private final RoleService roleService;

    public UserHomeTools(UserService userService, HomeService homeService, RoleService roleService) {
        this.userService = userService;
        this.homeService = homeService;
        this.roleService = roleService;
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

    public void RemoveUserFromHome(User user){
        // buscar si el usuario esta en algun hogar
        Home home = homeService.findHomeByUser(user);

        if(home == null) return;

        // validar el rol del usuario
        if(user.getRolId().contains(roleService.getRoleByName("Encargado"))){
            // se elimina el encargado de la casa, que es el usuario
            homeService.removeHomeAdmin(home);

            // se eliminan los roles del usuario
            userService.deleteRoleToUser(user, roleService.getRoleByName("Encargado"));
            userService.deleteRoleToUser(user, roleService.getRoleByName("Residente"));

            return;
        }

        // si el usuario pertenece a un hogar y no es encargado, se elimina de la lista de miembros
        homeService.removeHomeMembers(home, user);

        // quitar los roles del usuario
        userService.deleteRoleToUser(user, roleService.getRoleByName("Residente"));
    }

}
