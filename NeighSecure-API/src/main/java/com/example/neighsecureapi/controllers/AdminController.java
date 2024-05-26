package com.example.neighsecureapi.controllers;


import com.example.neighsecureapi.domain.dtos.HomeRegisterDataDTO;
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
import com.example.neighsecureapi.utils.UserHomeTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/neighSecure/admin")
@Slf4j
public class AdminController {

    private final UserService userService;
    private final HomeService homeService;
    private final FilterUserTools filterUserTools;
    private final RoleService roleService;
    private final EntryService entryService;
    private final TerminalService terminalService;
    private final PermissionService permissionService;
    private final UserHomeTools userHomeTools;

    public AdminController(UserService userService, HomeService homeService, FilterUserTools filterUserTools, RoleService roleService, HomeRepository homeRepository, EntryRepository entryRepository, EntryService entryService, TerminalService terminalService, PermissionService permissionService, UserHomeTools userHomeTools) {
        this.userService = userService;
        this.homeService = homeService;
        this.filterUserTools = filterUserTools;
        this.roleService = roleService;
        this.entryService = entryService;
        this.terminalService = terminalService;
        this.permissionService = permissionService;
        this.userHomeTools = userHomeTools;
    }

    // USER SECTION --------------------------------------------------------------

    @GetMapping("/users")
    public ResponseEntity<GeneralResponse> getAllUsers() {

        // se mapea la lista de usuarios a un dto para no enviar la data sensible
        // TODO: VER SI HAY PEDILLOS CON EL HOME NUMBER POR QUE UN USUARIO SOLO PUEDE ESTAR EN UNA CASA
        List<UserResponseDTO> users = userService.getAllUsers()
                .stream().map(user -> {
                    UserResponseDTO userResponseDTO = new UserResponseDTO();
                    userResponseDTO.setId(user.getId());
                    userResponseDTO.setName(user.getName());
                    userResponseDTO.setEmail(user.getEmail());
                    userResponseDTO.setPhone(user.getPhone());
                    userResponseDTO.setDui(user.getDui());
                    // obtener el numero de casa del usuario
                    userResponseDTO.setHomeNumber(homeService.findHomeByUser(user) != null ? homeService.findHomeByUser(user).getHomeNumber() : 0);

                    return userResponseDTO;
                }).toList();

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Usuarios obtenidos con exito")
                        .data(users)
                        .build(),
                HttpStatus.OK
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
                        .data(user)
                        .build(),
                HttpStatus.OK
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

        // TODO: AGREGAR LOS CAMPOS QUE FALTAN DE LA DATA DE LA VISTA
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
    public ResponseEntity<GeneralResponse> registerHome(@RequestBody HomeRegisterDataDTO info) {

        Home home = homeService.findHomeByAddressAndHomeNumber(info.getAddress(), info.getHomeNumber());

        if(home != null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("La casa ya esta registrada")
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }

        // PRUEBA ---------------------------------------------------

        // Obtener el usuario administrador y cambiar su rol a "Encargado"
        User adminUser = userService.getUser(info.getUserAdmin());
        Role adminRole = roleService.getRoleByName("Encargado");
        userService.updateRoleToUser(adminUser, adminRole);

        // Crear una lista para almacenar los miembros de la casa
        List<User> homeMembers = new ArrayList<>();

        // Para cada ID de usuario en la lista de miembros de la casa
        for(UUID memberId : info.getHomeMembers()) {
            // Obtener el usuario y cambiar su rol a "Residente"
            User memberUser = userService.getUser(memberId);
            Role memberRole = roleService.getRoleByName("Residente");
            userService.updateRoleToUser(memberUser, memberRole);

            // Agregar el usuario a la lista de miembros de la casa
            homeMembers.add(memberUser);
        }

        // Crear un nuevo HomeRegisterDTO y llenarlo con la información de HomeRegisterDataDTO y los usuarios obtenidos
        HomeRegisterDTO homeRegisterDTO = new HomeRegisterDTO();
        homeRegisterDTO.setHomeNumber(info.getHomeNumber());
        homeRegisterDTO.setAddress(info.getAddress());
        homeRegisterDTO.setMembersNumber(info.getMembersNumber());
        homeRegisterDTO.setUserAdmin(adminUser);
        homeRegisterDTO.setHomeMembers(homeMembers);

        // Guardar la nueva casa
        homeService.saveHome(homeRegisterDTO);

        // TERMINA PRUEBA -------------------------------------------

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Casa registrada con exito")
                        .data(homeRegisterDTO)
                        .build(),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/homes/update/{homeId}")
    public ResponseEntity<GeneralResponse> updateHome(@PathVariable UUID homeId, @RequestBody HomeRegisterDataDTO info) {

        Home home = homeService.getHome(homeId);

        if(home == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Casa no encontrada")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        // PRUEBA ---------------------------------------------------

        // primero elimino el rol de los usuarios de la casa actualmente
        userService.updateRoleToUser(home.getHomeOwnerId(), roleService.getRoleByName("Visitante"));
        for(User member : home.getHomeMemberId()){
            userService.updateRoleToUser(member, roleService.getRoleByName("Visitante"));
        }

        // Obtener el nuevo usuario administrador y cambiar su rol a "Encargado"
        User adminUser = userService.getUser(info.getUserAdmin());
        Role adminRole = roleService.getRoleByName("Encargado");
        userService.updateRoleToUser(adminUser, adminRole);

        // Crear una lista para almacenar los nuevos miembros de la casa
        List<User> homeMembers = new ArrayList<>();

        // Para cada ID de usuario en la lista de miembros de la casa
        for(UUID memberId : info.getHomeMembers()) {
            // Obtener el usuario y cambiar su rol a "Residente"
            User memberUser = userService.getUser(memberId);
            Role memberRole = roleService.getRoleByName("Residente");
            userService.updateRoleToUser(memberUser, memberRole);

            // Agregar el usuario a la lista de miembros de la casa
            homeMembers.add(memberUser);
        }

        // Crear un nuevo HomeRegisterDTO y llenarlo con la información de HomeRegisterDataDTO y los usuarios obtenidos
        HomeRegisterDTO homeRegisterDTO = new HomeRegisterDTO();
        homeRegisterDTO.setHomeNumber(info.getHomeNumber());
        homeRegisterDTO.setAddress(info.getAddress());
        homeRegisterDTO.setMembersNumber(info.getMembersNumber());
        homeRegisterDTO.setUserAdmin(adminUser);
        homeRegisterDTO.setHomeMembers(homeMembers);

        // Guardar la nueva casa
        homeService.updateHome(home, homeRegisterDTO);

        // TERMINA PRUEBA -------------------------------------------

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Casa actualizada con exito")
                        .data(home.getHomeOwnerId())
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
