package com.example.neighsecureapi.utils;

import com.example.neighsecureapi.domain.dtos.userDTOs.UserResponseDTO;
import com.example.neighsecureapi.domain.entities.Role;
import com.example.neighsecureapi.domain.entities.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FilterUserTools {

    public List<UserResponseDTO> filterUsersByRole(List<User> users, Role roleFilter){
        List<UserResponseDTO> filteredUsers = new ArrayList<>();

        for (User user : users) {
            for (Role role : user.getRolId()) {
                if (role.equals(roleFilter)) {
                    UserResponseDTO userResponseDTO = new UserResponseDTO();
                    userResponseDTO.setName(user.getName());
                    userResponseDTO.setEmail(user.getEmail());
                    userResponseDTO.setPhone(user.getPhone());
                    userResponseDTO.setDui(user.getDui());
                    userResponseDTO.setHomeNumber(user.getHomeId() != null ? user.getHomeId().getHomeNumber() : 0);
                    filteredUsers.add(userResponseDTO);
                    break;
                }
            }
        }

        return filteredUsers;
    }

}
