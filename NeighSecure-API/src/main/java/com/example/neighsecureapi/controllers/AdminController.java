package com.example.neighsecureapi.controllers;


import com.example.neighsecureapi.domain.dtos.GeneralResponse;
import com.example.neighsecureapi.domain.dtos.userDTOs.DashboardAdmDTO;
import com.example.neighsecureapi.domain.entities.User;
import com.example.neighsecureapi.services.HomeService;
import com.example.neighsecureapi.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/neighSecure/admin")
public class AdminController {

    private final UserService userService;
    private final HomeService homeService;

    public AdminController(UserService userService, HomeService homeService) {
        this.userService = userService;
        this.homeService = homeService;
    }

    @GetMapping("/users")
    public ResponseEntity<GeneralResponse> getAllUsers() {
        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Usuarios obtenidos con exito")
                        .data(userService.getAllUsers())
                        .build(),
                HttpStatus.NOT_FOUND
        );
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<GeneralResponse> getUser(@PathVariable UUID userId) {

        User user = userService.getUser(userId);

        if(user == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Usuario no encontrado")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Usuarios obtenidos con exito")
                        .data(userService.getUser(userId))
                        .build(),
                HttpStatus.NOT_FOUND
        );
    }

    @GetMapping("/users/delete/{userId}")
    public ResponseEntity<GeneralResponse> deleteUser(@PathVariable UUID userId) {

        User user = userService.getUser(userId);

        if(user == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Usuario no encontrado")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        userService.deleteUser(userId);


        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Usuario eliminado con exito")
                        .build(),
                HttpStatus.OK
        );
    }

    @GetMapping("/dashboard")
    public ResponseEntity<GeneralResponse> getDashboard() {

        DashboardAdmDTO dashboard = new DashboardAdmDTO();

        // envia los usuarios para poder hacer las graficas
        dashboard.setUsers(userService.getAllUsers());

        // contar cuantos usuarios y casas hay
        dashboard.setTotalUsers(dashboard.getUsers().size());
        dashboard.setTotalHomes(homeService.getAllHomes().size());

        // envia el dto con la data necesaria para la vista
        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Dashboard obtenido con exito")
                        .data(dashboard)
                        .build(),
                HttpStatus.OK
        );
    }

}
