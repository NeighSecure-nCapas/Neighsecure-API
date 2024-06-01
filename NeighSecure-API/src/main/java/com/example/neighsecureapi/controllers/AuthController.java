package com.example.neighsecureapi.controllers;

import com.example.neighsecureapi.domain.dtos.GeneralResponse;
import com.example.neighsecureapi.domain.dtos.TokenDTO;
import com.example.neighsecureapi.domain.dtos.userDTOs.GoogleLoginDTO;
import com.example.neighsecureapi.domain.dtos.userDTOs.GoogleTokenInfo;
import com.example.neighsecureapi.domain.dtos.userDTOs.LoginUserDTO;
import com.example.neighsecureapi.domain.dtos.userDTOs.RegisterUserDTO;
import com.example.neighsecureapi.domain.entities.Home;
import com.example.neighsecureapi.domain.entities.Role;
import com.example.neighsecureapi.domain.entities.Token;
import com.example.neighsecureapi.domain.entities.User;
import com.example.neighsecureapi.services.RoleService;
import com.example.neighsecureapi.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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

    /*
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
    * */

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @PostMapping("/oauth2/google/authtoken")
    public Mono<ResponseEntity<GeneralResponse>> authenticateWithGoogle(@RequestBody String idTokenString) {
        WebClient webClient = WebClient.create();
        String googleUri = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=" + idTokenString;
        // https://www.googleapis.com/token

        /*
        .headers(header ->{
                    header.add("code", "codigo_custom");
                    header.add("client_id", clientId);
                    header.add("client_secret", clientSecret);
                    header.add("redirect_uri", "http://localhost:8080/neighSecure/auth/oauth2/google");
                    header.add("grant_type", "authorization_code");
                })
        * */

        return webClient.get()
                .uri(googleUri)
                .retrieve()
                .bodyToMono(GoogleTokenInfo.class)
                .flatMap(tokenInfo -> {
                    String email = tokenInfo.getEmail();
                    String name = tokenInfo.getName();

                    // Use or store profile information

                    User user = userService.findUserByEmail(email);

                    if(user == null) {
                        Role rol = roleService.getRoleByName("Visitante");

                        RegisterUserDTO registerUserDTO = new RegisterUserDTO();
                        registerUserDTO.setEmail(email);
                        registerUserDTO.setName(name);
                        registerUserDTO.setDui(""); // se pide luego del login si es un register
                        registerUserDTO.setPhone(""); // se pide luego del login si es un register

                        userService.saveUser(registerUserDTO, rol);

                        // despues de creado, se crea el token para inicar sesión y se retorna httpStatus created para identificar
                        User userRes = userService.findUserByEmail(email);

                        try {
                            Token token = userService.registerToken(userRes);
                            return Mono.just(new ResponseEntity<>(
                                    new GeneralResponse.Builder()
                                            .message("Usuario registrado con exito, sesión iniciada")
                                            .data(new TokenDTO(token))
                                            .build(),
                                    HttpStatus.CREATED
                            ));
                        } catch (Exception e) {
                            //e.printStackTrace();
                            return Mono.just(new ResponseEntity<>(
                                    new GeneralResponse.Builder()
                                            .message("Usuario creado con éxito, error al registrar el token")
                                            .build(),
                                    HttpStatus.INTERNAL_SERVER_ERROR));
                        }

                    }

                    // si ya existe el usuario hace login

                    try {
                        Token token = userService.registerToken(user);
                        return Mono.just(new ResponseEntity<>(
                                new GeneralResponse.Builder()
                                        .message("Usuario encontrado")
                                        .data(new TokenDTO(token))
                                        .build(),
                                HttpStatus.OK
                        ));
                    } catch (Exception e) {
                        //e.printStackTrace();
                        return Mono.just(new ResponseEntity<>(
                                new GeneralResponse.Builder()
                                        .message("Usuario creado con éxito, error al registrar el token")
                                        .build(),
                                HttpStatus.INTERNAL_SERVER_ERROR
                        ));
                    }

                })
                .onErrorResume(e -> {
                    // e.printStackTrace();
                    return Mono.just(new ResponseEntity<>(
                            new GeneralResponse.Builder()
                                    .message("Error al autenticar con Google")
                                    .build(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
                });
    }

    @PostMapping("/oauth2/google/code")
    public Mono<ResponseEntity<GeneralResponse>> authenticateWithGoogleCode(@RequestBody String authorizationCode) {
        WebClient webClient = WebClient.create();
        String googleUri = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", authorizationCode);
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("redirect_uri", "http://localhost:8080/neighSecure/auth/oauth2/google/authtoken");
        formData.add("grant_type", "authorization_code");

        return webClient.post()
                .uri(googleUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(GoogleTokenInfo.class)
                .flatMap(tokenInfo -> {
                    // Aquí puedes usar el token de acceso para obtener información del perfil del usuario
                    return Mono.just(new ResponseEntity<>(
                            new GeneralResponse.Builder()
                                    .message("Petición realizada con éxito")
                                    .build(),
                            HttpStatus.OK
                    ));
                })
                .onErrorResume(e -> {
                    return Mono.just(new ResponseEntity<>(
                            new GeneralResponse.Builder()
                                    .message("Error al autenticar con Google")
                                    .build(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
                });
    }

}
