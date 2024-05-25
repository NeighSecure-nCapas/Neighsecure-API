package com.example.neighsecureapi.domain.dtos.userDTOs;

import com.example.neighsecureapi.domain.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardAdmDTO {
    int totalUsers;
    int totalHomes;
    List<User> users;
}
