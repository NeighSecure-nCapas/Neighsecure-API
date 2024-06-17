package com.example.neighsecureapi.controllers;

import com.example.neighsecureapi.domain.dtos.GeneralResponse;
import com.example.neighsecureapi.domain.dtos.TokenDTO;
import com.example.neighsecureapi.domain.dtos.userDTOs.*;
import com.example.neighsecureapi.domain.entities.Home;
import com.example.neighsecureapi.domain.entities.Role;
import com.example.neighsecureapi.domain.entities.Token;
import com.example.neighsecureapi.domain.entities.User;
import com.example.neighsecureapi.repositories.TokenRepository;
import com.example.neighsecureapi.services.*;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@CrossOrigin(origins = "*")
@RestController
@Log4j2
@RequestMapping("/neighSecure/auth")
public class AuthController {

    private final UserService userService;
    private final RoleService roleService;
    private final AuthService authService;
    private final TokenService tokenService;
    private final HomeService homeService;

    public AuthController(UserService userService, RoleService roleService, AuthService authService, TokenService tokenService, HomeService homeService) {
        this.userService = userService;
        this.roleService = roleService;
        this.authService = authService;
        this.tokenService = tokenService;
        this.homeService = homeService;
    }

    @PostMapping("/register")
    public ResponseEntity<GeneralResponse> register(@RequestBody @Valid RegisterUserDTO registerUserDTO) {

        // TODO: implementar registro con google
        User user = userService.findUserByEmail(registerUserDTO.getEmail());

        if (user != null) {
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

        if (user == null) {
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

//    @PostMapping("/oauth2/google/authtoken")
//    public Mono<ResponseEntity<GeneralResponse>> authenticateWithGoogle(@RequestBody String idTokenString) {
//        log.info("ID Token: {}", idTokenString);
//        WebClient webClient = WebClient.create();
//        String googleUri = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=" + idTokenString;
//        // https://www.googleapis.com/token
//
//        /*
//        .headers(header ->{
//                    header.add("code", "codigo_custom");
//                    header.add("client_id", clientId);
//                    header.add("client_secret", clientSecret);
//                    header.add("redirect_uri", "http://localhost:8080/neighSecure/auth/oauth2/google");
//                    header.add("grant_type", "authorization_code");
//                })
//        * */
//
//        return webClient.get()
//                .uri(googleUri)
//                .retrieve()
//                .bodyToMono(GoogleTokenInfo.class)
//                .flatMap(tokenInfo -> {
//                    String email = tokenInfo.getEmail();
//                    String name = tokenInfo.getName();
//
//                    // Use or store profile information
//
//                    User user = userService.findUserByEmail(email);
//
//                    if (user == null) {
//                        Role rol = roleService.getRoleByName("Visitante");
//
//                        RegisterUserDTO registerUserDTO = new RegisterUserDTO();
//                        registerUserDTO.setEmail(email);
//                        registerUserDTO.setName(name);
//                        registerUserDTO.setDui(""); // se pide luego del login si es un register
//                        registerUserDTO.setPhone(""); // se pide luego del login si es un register
//
//                        userService.saveUser(registerUserDTO, rol);
//
//                        // despues de creado, se crea el token para inicar sesión y se retorna httpStatus created para identificar
//                        User userRes = userService.findUserByEmail(email);
//
//                        try {
//                            Token token = userService.registerToken(userRes);
//                            return Mono.just(new ResponseEntity<>(
//                                    new GeneralResponse.Builder()
//                                            .message("Usuario registrado con exito, sesión iniciada")
//                                            .data(new TokenDTO(token))
//                                            .build(),
//                                    HttpStatus.CREATED
//                            ));
//                        } catch (Exception e) {
//                            //e.printStackTrace();
//                            return Mono.just(new ResponseEntity<>(
//                                    new GeneralResponse.Builder()
//                                            .message("Usuario creado con éxito, error al registrar el token")
//                                            .build(),
//                                    HttpStatus.INTERNAL_SERVER_ERROR));
//                        }
//
//                    }
//
//                    // si ya existe el usuario hace login
//
//                    try {
//                        Token token = userService.registerToken(user);
//                        return Mono.just(new ResponseEntity<>(
//                                new GeneralResponse.Builder()
//                                        .message("Usuario encontrado")
//                                        .data(new TokenDTO(token))
//                                        .build(),
//                                HttpStatus.OK
//                        ));
//                    } catch (Exception e) {
//                        //e.printStackTrace();
//                        return Mono.just(new ResponseEntity<>(
//                                new GeneralResponse.Builder()
//                                        .message("Usuario creado con éxito, error al registrar el token")
//                                        .build(),
//                                HttpStatus.INTERNAL_SERVER_ERROR
//                        ));
//                    }
//
//                })
//                .onErrorResume(e -> {
//                    // e.printStackTrace();
//                    return Mono.just(new ResponseEntity<>(
//                            new GeneralResponse.Builder()
//                                    .message("Error al autenticar con Google")
//                                    .build(),
//                            HttpStatus.INTERNAL_SERVER_ERROR
//                    ));
//                });
//    }

    @GetMapping("/google/redirect")
    public Mono<ResponseEntity<GeneralResponse>> authenticateWithGoogleCode(@RequestParam("code") String authorizationCode) {
        log.info("Authorization Code: {}", authorizationCode);


        return authService.exchangeCodeForAccessToken(authorizationCode)
                .flatMap(authService::fetchGoogleProfile)
                .flatMap(googleUserInfo -> {

                    log.info("Getting user info... {}", googleUserInfo );

                    String email = googleUserInfo.getEmail();
                    String name = googleUserInfo.getName();

                    // Use or store profile information
                    User user = userService.findUserByEmail(email);

                    if (user == null) {
                        log.info("User not found, creating user... {}", email);
                        Role rol = roleService.getRoleByName("Visitante");

                        RegisterUserDTO registerUserDTO = new RegisterUserDTO();

                        registerUserDTO.setEmail(email);
                        registerUserDTO.setName(name);
                        registerUserDTO.setDui(""); // se pide luego del login si es un register
                        registerUserDTO.setPhone(""); // se pide luego del login si es un register

                        userService.saveUser(registerUserDTO, rol);

                        // despues de creado, se crea el token para inicar sesión y se retorna httpStatus created para identificar
                        User userRes = userService.findUserByEmail(email);

                        log.info("User created, new user found{}", userRes);
                        try {
                            Token token = userService.registerToken(userRes);
                            return Mono.just(new ResponseEntity<>(
                                    new GeneralResponse.Builder()
                                            .message("User register successfully and session started")
                                            .data(new TokenDTO(token))
                                            .build(),
                                    HttpStatus.CREATED
                            ));
                        } catch (Exception e) {
                            //e.printStackTrace();
                            return Mono.just(new ResponseEntity<>(
                                    new GeneralResponse.Builder()
                                            .message("User register successfully but there was an error registering the token")
                                            .build(),
                                    HttpStatus.INTERNAL_SERVER_ERROR));
                        }

                    }

                    log.info("User google info found, responding {}", user);
                    // if user already exists, login
                    try {
                        Token token = userService.registerToken(user);
                        return Mono.just(new ResponseEntity<>(
                                new GeneralResponse.Builder()
                                        .message("User found")
                                        .data(new TokenDTO(token))
                                        .build(),
                                HttpStatus.OK
                        ));
                    } catch (Exception e) {
                        //e.printStackTrace();
                        return Mono.just(new ResponseEntity<>(
                                new GeneralResponse.Builder()
                                        .message("User register successfully but there was an error registering the token")
                                        .build(),
                                HttpStatus.INTERNAL_SERVER_ERROR
                        ));
                    }
                })
                .onErrorResume(e -> {
                    // On Error
                    GeneralResponse response = new GeneralResponse.Builder()
                            .message("There was an error fetching user info from Google")
                            .build();
                    return Mono.just(new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR));
                });
    }

    @GetMapping("/google/redirect-mobile")
    public Mono<ResponseEntity<GeneralResponse>> authWithGoogleFromMobile(@RequestParam("access_token") String accessToken) {

        log.info("Access Token: {}", accessToken);

        return authService.fetchGoogleProfile(accessToken)
                .flatMap(googleUserInfo -> {

                    log.info("Getting user info... {}", googleUserInfo );

                    String email = googleUserInfo.getEmail();
                    String name = googleUserInfo.getName();

                    // Use or store profile information
                    User user = userService.findUserByEmail(email);

                    if (user == null) {
                        log.info("User not found, creating user... {}", email);
                        Role rol = roleService.getRoleByName("Visitante");

                        RegisterUserDTO registerUserDTO = new RegisterUserDTO();

                        registerUserDTO.setEmail(email);
                        registerUserDTO.setName(name);
                        registerUserDTO.setDui(""); // se pide luego del login si es un register
                        registerUserDTO.setPhone(""); // se pide luego del login si es un register

                        userService.saveUser(registerUserDTO, rol);

                        // despues de creado, se crea el token para inicar sesión y se retorna httpStatus created para identificar
                        User userRes = userService.findUserByEmail(email);

                        log.info("User created, new user found{}", userRes);
                        try {
                            Token token = userService.registerToken(userRes);
                            return Mono.just(new ResponseEntity<>(
                                    new GeneralResponse.Builder()
                                            .message("User register successfully and session started")
                                            .data(new TokenDTO(token))
                                            .build(),
                                    HttpStatus.CREATED
                            ));
                        } catch (Exception e) {
                            //e.printStackTrace();
                            return Mono.just(new ResponseEntity<>(
                                    new GeneralResponse.Builder()
                                            .message("User register successfully but there was an error registering the token")
                                            .build(),
                                    HttpStatus.INTERNAL_SERVER_ERROR));
                        }

                    }

                    log.info("User google info found, responding {}", user);
                    // if user already exists, login
                    try {
                        Token token = userService.registerToken(user);
                        return Mono.just(new ResponseEntity<>(
                                new GeneralResponse.Builder()
                                        .message("User found")
                                        .data(new TokenDTO(token))
                                        .build(),
                                HttpStatus.OK
                        ));
                    } catch (Exception e) {
                        //e.printStackTrace();
                        return Mono.just(new ResponseEntity<>(
                                new GeneralResponse.Builder()
                                        .message("User register successfully but there was an error registering the token")
                                        .build(),
                                HttpStatus.INTERNAL_SERVER_ERROR
                        ));
                    }
                })
                .onErrorResume(e -> {
                    // On Error
                    GeneralResponse response = new GeneralResponse.Builder()
                            .message("There was an error fetching user info from Google")
                            .build();
                    return Mono.just(new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR));
                });
    }

    @GetMapping("/whoami")
    public ResponseEntity<GeneralResponse> whoAmI(@RequestHeader("Authorization") String bearerToken) {

        if (bearerToken == null || bearerToken.isEmpty()) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("No token provided")
                            .build(),
                    HttpStatus.UNAUTHORIZED
            );
        }

        String token = bearerToken.substring(7);

        Token tokenEntity = tokenService.findTokenBycontent(token);

        User user = userService.findUserByToken(tokenEntity);

        if (user == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("User not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        // pasar la data a un DTO
        WhoAmIDTO whoAmIDTO = new WhoAmIDTO();
        whoAmIDTO.setUserId(user.getId());
        whoAmIDTO.setUsername(user.getName());
        whoAmIDTO.setEmail(user.getEmail());
        whoAmIDTO.setRoles(user.getRolId());
        whoAmIDTO.setPhoneNumber(user.getPhone());
        whoAmIDTO.setDui(user.getDui());

        // buscar la casa a la que pertenece
        Home home = homeService.findHomeByUser(user);

        whoAmIDTO.setHomeId(home.getId());

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("User found")
                        .data(whoAmIDTO)
                        .build(),
                HttpStatus.OK
        );
    }
}
