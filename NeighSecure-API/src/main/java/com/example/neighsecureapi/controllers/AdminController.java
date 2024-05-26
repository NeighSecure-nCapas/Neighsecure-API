package com.example.neighsecureapi.controllers;


import com.example.neighsecureapi.domain.dtos.entryDTOs.EntryBoardAdmDTO;
import com.example.neighsecureapi.domain.dtos.GeneralResponse;
import com.example.neighsecureapi.domain.dtos.HomeRegisterDTO;
import com.example.neighsecureapi.domain.dtos.userDTOs.DashboardAdmDTO;
import com.example.neighsecureapi.domain.dtos.userDTOs.UserResponseDTO;
import com.example.neighsecureapi.domain.entities.Entry;
import com.example.neighsecureapi.domain.entities.Home;
import com.example.neighsecureapi.domain.entities.Role;
import com.example.neighsecureapi.domain.entities.User;
import com.example.neighsecureapi.repositories.EntryRepository;
import com.example.neighsecureapi.repositories.HomeRepository;
import com.example.neighsecureapi.services.*;
import com.example.neighsecureapi.utils.FilterUserTools;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/neighSecure/admin")
public class AdminController {

    private final UserService userService;
    private final HomeService homeService;
    private final FilterUserTools filterUserTools;
    private final RoleService roleService;
    private final EntryService entryService;
    private final TerminalService terminalService;
    private final PermissionService permissionService;

    public AdminController(UserService userService, HomeService homeService, FilterUserTools filterUserTools, RoleService roleService, HomeRepository homeRepository, EntryRepository entryRepository, EntryService entryService, TerminalService terminalService, PermissionService permissionService) {
        this.userService = userService;
        this.homeService = homeService;
        this.filterUserTools = filterUserTools;
        this.roleService = roleService;
        this.entryService = entryService;
        this.terminalService = terminalService;
        this.permissionService = permissionService;
    }

    // USER SECTION --------------------------------------------------------------

    @GetMapping("/users")
    public ResponseEntity<GeneralResponse> getAllUsers() {

        // TODO: VERIFICAR SI TENGO QUE MANDAR EL UUID SIEMPRE QUE MANDO USUARIOS
        // se mapea la lista de usuarios a un dto para no enviar la data sensible
        List<UserResponseDTO> users = userService.getAllUsers()
                .stream().map(user -> {
                    UserResponseDTO userResponseDTO = new UserResponseDTO();
                    userResponseDTO.setId(user.getId());
                    userResponseDTO.setName(user.getName());
                    userResponseDTO.setEmail(user.getEmail());
                    userResponseDTO.setPhone(user.getPhone());
                    userResponseDTO.setDui(user.getDui());
                    userResponseDTO.setHomeNumber(user.getHomeId() != null ? user.getHomeId().getHomeNumber() : 0);
                    return userResponseDTO;
                }).toList();

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Usuarios obtenidos con exito")
                        .data(users)
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

    @PatchMapping("/users/delete/{userId}")
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

    @GetMapping("/users/role/{role}")
    public ResponseEntity<GeneralResponse> getUsersByRole(@PathVariable String role) {

        String roleFixed = role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();

        Role roleFilter = roleService.getRoleByName(roleFixed);

        List<UserResponseDTO> users = filterUserTools.filterUsersByRole(userService.getAllUsers(), roleFilter)
                .stream().toList();


        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Usuarios obtenidos con exito")
                        .data(users)
                        .build(),
                HttpStatus.OK
        );
    }

    @GetMapping("/users/dui_email/{dui}/{email}")
    public ResponseEntity<GeneralResponse> getUsersByDuiOrEmail(@PathVariable String dui, @PathVariable String email) {

        User user = userService.findUserByEmailAndDui(email, dui);

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
                        .data(user)
                        .build(),
                HttpStatus.OK
        );
    }

    // DASHBOARD SECTION --------------------------------------------------------------

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

    // HOME SECTION --------------------------------------------------------------

    @GetMapping("/homes")
    public ResponseEntity<GeneralResponse> getAllHomes() {

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Casas obtenidas con exito")
                        .data(homeService.getAllHomes())
                        .build(),
                HttpStatus.OK
        );
    }

    @GetMapping("/homes/{homeId}")
    public ResponseEntity<GeneralResponse> getHome(@PathVariable UUID homeId) {

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Casa obtenida con exito")
                        .data(homeService.getHome(homeId))
                        .build(),
                HttpStatus.OK
        );
    }

    @PostMapping("/homes/register")
    public ResponseEntity<GeneralResponse> registerHome(@RequestBody HomeRegisterDTO info) {

        Home home = homeService.findHomeByAddressAndHomeNumber(info.getAddress(), info.getHomeNumber());

        if(home != null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("La casa ya esta registrada")
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }

        homeService.saveHome(info);

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Casa registrada con exito")
                        .build(),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/homes/update/{homeId}")
    public ResponseEntity<GeneralResponse> updateHome(@PathVariable UUID homeId, @RequestBody HomeRegisterDTO info) {

        Home home = homeService.getHome(homeId);

        if(home == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Casa no encontrada")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        homeService.updateHome(home, info);

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Casa actualizada con exito")
                        .build(),
                HttpStatus.OK
        );
    }

    @PatchMapping("/homes/delete/{homeId}")
    public ResponseEntity<GeneralResponse> deleteHome(@PathVariable UUID homeId) {

        Home home = homeService.getHome(homeId);

        if(home == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Casa no encontrada")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        homeService.deleteHome(home);

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Casa eliminada con exito")
                        .build(),
                HttpStatus.OK
        );
    }

    // ENTRY SECTION --------------------------------------------------------------
    @GetMapping("/entries")
    public ResponseEntity<GeneralResponse> getAllEntries() {

        // se mapea la lista de entradas a un dto para no enviar la data sensible y dar formato

        List<EntryBoardAdmDTO> entries = entryService.getAllEntries()
                .stream().map(entry -> {
                    EntryBoardAdmDTO entryBoardAdmDTO = new EntryBoardAdmDTO();
                    entryBoardAdmDTO.setId(entry.getId());
                    entryBoardAdmDTO.setDate(entry.getEntryDate());
                    entryBoardAdmDTO.setUser(userService.getUser(permissionService.getPermission(entry.getPermissionId().getId()).getUserId().getId()).getName());
                    entryBoardAdmDTO.setHomeNumber(homeService.getHome(permissionService.getPermission(entry.getPermissionId().getId()).getHomeId().getId()).getHomeNumber());
                    entryBoardAdmDTO.setEntryType(terminalService.getTerminalById(entry.getTerminalId().getTerminalId()).getEntryType());
                    return entryBoardAdmDTO;
                }).toList();


        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Entradas obtenidas con exito")
                        .data(entries)
                        .build(),
                HttpStatus.OK
        );
    }

    @GetMapping("/entries/{entryId}")
    public ResponseEntity<GeneralResponse> getEntry(@PathVariable UUID entryId) {

            EntryBoardAdmDTO entryFormat = new EntryBoardAdmDTO();

            Entry entry = entryService.getEntry(entryId);

            if(entry == null) {
                return new ResponseEntity<>(
                        new GeneralResponse.Builder()
                                .message("Entrada no encontrada")
                                .build(),
                        HttpStatus.NOT_FOUND
                );
            }

            // se mapea la entrada a un dto para no enviar la data sensible y dar formato

            entryFormat.setId(entry.getId());
            entryFormat.setDate(entry.getEntryDate());
            entryFormat.setUser(userService.getUser(permissionService.getPermission(entry.getPermissionId().getId()).getUserId().getId()).getName());
            entryFormat.setHomeNumber(homeService.getHome(permissionService.getPermission(entry.getPermissionId().getId()).getHomeId().getId()).getHomeNumber());
            entryFormat.setEntryType(terminalService.getTerminalById(entry.getTerminalId().getTerminalId()).getEntryType());


            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Entrada obtenida con exito")
                            .data(entryFormat)
                            .build(),
                    HttpStatus.OK
            );
    }

    @DeleteMapping("/entries/delete/{entryId}")
    public ResponseEntity<GeneralResponse> deleteEntry(@PathVariable UUID entryId){

            Entry entry = entryService.getEntry(entryId);

            if(entry == null) {
                return new ResponseEntity<>(
                        new GeneralResponse.Builder()
                                .message("Entrada no encontrada")
                                .build(),
                        HttpStatus.NOT_FOUND
                );
            }

            entryService.deleteEntry(entryId);

            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Entrada eliminada con exito")
                            .build(),
                    HttpStatus.OK
            );
    }

}
