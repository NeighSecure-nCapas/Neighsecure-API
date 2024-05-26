package com.example.neighsecureapi.utils;

import com.example.neighsecureapi.domain.dtos.userDTOs.UserResponseDTO;
import com.example.neighsecureapi.domain.entities.Role;
import com.example.neighsecureapi.domain.entities.User;
import com.example.neighsecureapi.services.HomeService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FilterUserTools {

    private final HomeService homeService;

    public FilterUserTools(HomeService homeService) {
        this.homeService = homeService;
    }

    public List<UserResponseDTO> filterUsersByRole(List<User> users, Role roleFilter){
        List<UserResponseDTO> filteredUsers = new ArrayList<>();

        for (User user : users) {
            for (Role role : user.getRolId()) {
                if (role.equals(roleFilter)) {
                    UserResponseDTO userResponseDTO = new UserResponseDTO();
                    userResponseDTO.setId(user.getId());
                    userResponseDTO.setName(user.getName());
                    userResponseDTO.setEmail(user.getEmail());
                    userResponseDTO.setPhone(user.getPhone());
                    userResponseDTO.setDui(user.getDui());
                    userResponseDTO.setHomeNumber(homeService.findHomeByUser(user) != null ? homeService.findHomeByUser(user).getHomeNumber() : 0);
                    filteredUsers.add(userResponseDTO);
                    break;
                }
            }
        }

        return filteredUsers;
    }

}
