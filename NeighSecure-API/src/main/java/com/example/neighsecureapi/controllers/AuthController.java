package com.example.neighsecureapi.controllers;

import com.example.neighsecureapi.domain.dtos.GeneralResponse;
import com.example.neighsecureapi.domain.dtos.TokenDTO;
import com.example.neighsecureapi.domain.dtos.userDTOs.GoogleLoginDTO;
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

        // TODO: implementar registro con google
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

    @PostMapping("/oauth2/google")
    public ResponseEntity<GeneralResponse> loginGoogle(@RequestBody @Valid GoogleLoginDTO googleData) {

        User user = userService.findUserByEmail(googleData.getEmail());

        if(user == null) {
            Role rol = roleService.getRoleByName("Visitante");

            RegisterUserDTO registerUserDTO = new RegisterUserDTO();
            registerUserDTO.setEmail(googleData.getEmail());
            registerUserDTO.setName(googleData.getName());
            registerUserDTO.setDui("");// se pide luego del login si es un register
            registerUserDTO.setPhone("");// se pide luego del login si es un register

            userService.saveUser(registerUserDTO, rol);

            // despues de creado, se crea el token para inicar sesión y se retorna httpStatus created para identificar
            User userRes = userService.findUserByEmail(googleData.getEmail());

            try {
                Token token = userService.registerToken(userRes);
                return new ResponseEntity<>(
                        new GeneralResponse.Builder()
                                .message("Usuario registrado con exito, sesión iniciada")
                                .data(new TokenDTO(token))
                                .build(),
                        HttpStatus.CREATED
                );
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(
                        new GeneralResponse.Builder()
                                .message("Usuario creado con éxito, error al registrar el token")
                                .build(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }

        // si ya existe el usuario hace login

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
