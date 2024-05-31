package com.example.neighsecureapi.controllers;

import com.example.neighsecureapi.domain.dtos.GeneralResponse;
import com.example.neighsecureapi.domain.dtos.TokenDTO;
import com.example.neighsecureapi.domain.dtos.userDTOs.LoginUserDTO;
import com.example.neighsecureapi.domain.dtos.userDTOs.RegisterUserDTO;
import com.example.neighsecureapi.domain.entities.Home;
import com.example.neighsecureapi.domain.entities.Role;
import com.example.neighsecureapi.domain.entities.Token;
import com.example.neighsecureapi.domain.entities.User;
import com.example.neighsecureapi.services.RoleService;
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
    private final RoleService roleService;

    public AuthController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
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

        Role rol = roleService.getRoleByName("Visitante");

        userService.saveUser(registerUserDTO, rol);

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Usuario registrado con exito")
                        .build(),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    public ResponseEntity<GeneralResponse> login(@RequestBody @Valid LoginUserDTO loginUserDTO) {

        // TODO: implementar login con google
        User user = userService.findUserByEmail(loginUserDTO.getEmail());

        if(user == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Usuario no encontrado")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }


        try {
            Token token = userService.registerToken(user);
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Usuario encontrado")
                            .data(new TokenDTO(token))
                            .build(),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
