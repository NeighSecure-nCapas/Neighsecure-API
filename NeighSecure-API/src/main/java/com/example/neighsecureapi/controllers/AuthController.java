package com.example.neighsecureapi.controllers;

import com.example.neighsecureapi.domain.dtos.GeneralResponse;
import com.example.neighsecureapi.domain.dtos.userDTOs.RegisterUserDTO;
import com.example.neighsecureapi.domain.entities.Home;
import com.example.neighsecureapi.domain.entities.User;
import com.example.neighsecureapi.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/neighSecure/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<GeneralResponse> register(@RequestBody @Valid RegisterUserDTO registerUserDTO) {

        User user = userService.findUserByEmail(registerUserDTO.getEmail());

        if(user != null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("El usuario ya existe")
                            .build(),
                    HttpStatus.CONFLICT
            );
        }

        userService.saveUser(registerUserDTO);

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Usuario registrado con exito")
                        .build(),
                HttpStatus.CREATED
        );
    }

}
