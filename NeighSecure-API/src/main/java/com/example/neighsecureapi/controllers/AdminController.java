package com.example.neighsecureapi.controllers;


import com.example.neighsecureapi.domain.dtos.entryDTOs.PresentationEntryDTO;
import com.example.neighsecureapi.domain.dtos.entryDTOs.PresentationEntryDetailsDTO;
import com.example.neighsecureapi.domain.dtos.homeDTOs.*;
import com.example.neighsecureapi.domain.dtos.entryDTOs.EntryBoardAdmDTO;
import com.example.neighsecureapi.domain.dtos.GeneralResponse;
import com.example.neighsecureapi.domain.dtos.userDTOs.DashboardAdmDTO;
import com.example.neighsecureapi.domain.dtos.userDTOs.PresentationUserDetailsDTO;
import com.example.neighsecureapi.domain.dtos.userDTOs.RoleUpdateDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@CrossOrigin(origins = "*")
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

    @PreAuthorize("hasAuthority('Administrador')")
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
                        .message("Users obtained successfully")
                        .data(users)
                        .build(),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasAuthority('Administrador')")
    @GetMapping("/users/{userId}")
    public ResponseEntity<GeneralResponse> getUser(@PathVariable UUID userId) {

        User user = userService.getUser(userId);

        if(user == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("User not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        PresentationUserDetailsDTO userP = new PresentationUserDetailsDTO();
        userP.setId(user.getId());
        userP.setName(user.getName());
        userP.setEmail(user.getEmail());
        userP.setDui(user.getDui());
        userP.setPhoneNumber(user.getPhone());
        userP.setRoles(user.getRolId());

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("User obtained successfully")
                        .data(userP)
                        .build(),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasAuthority('Administrador')")
    @PatchMapping("/users/delete/{userId}")
    public ResponseEntity<GeneralResponse> deleteUser(@PathVariable UUID userId) {

        User user = userService.getUser(userId);

        if(user == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("User not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }
        // eliminar al usuario de la casa si es que pertenece a una
        userHomeTools.RemoveUserFromHome(user);

        userService.deleteUser(userId);


        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("User deleted successfully")
                        .build(),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasAuthority('Administrador')")
    @GetMapping("/users/role/{role}")
    public ResponseEntity<GeneralResponse> getUsersByRole(
            @PathVariable String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        String roleFixed = role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();

        Role roleFilter = roleService.getRoleByName(roleFixed);

        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userService.getAllUsersByRole(pageable, roleFilter);

        //List<UserResponseDTO> users = filterUserTools.filterUsersByRole(userService.getAllUsers(), roleFilter)
                //.stream().toList();
        List<UserResponseDTO> usersDTO = users
                .stream().map(user -> {
                    UserResponseDTO userResponseDTO = new UserResponseDTO();
                    userResponseDTO.setId(user.getId());
                    userResponseDTO.setName(user.getName());
                    userResponseDTO.setEmail(user.getEmail());
                    userResponseDTO.setPhone(user.getPhone());
                    userResponseDTO.setDui(user.getDui());
                    userResponseDTO.setHomeNumber(homeService.findHomeByUser(user) != null ? homeService.findHomeByUser(user).getHomeNumber() : 0);
                    return userResponseDTO;
                }).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("users", usersDTO);
        response.put("totalPages", users.getTotalPages());

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Users obtained successfully")
                        .data(response)
                        .build(),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasAuthority('Administrador')")
    @GetMapping("/users/{dui}/{email}")
    public ResponseEntity<GeneralResponse> getUsersByDuiOrEmail(@PathVariable String dui, @PathVariable String email) {

        User user = userService.findUserByEmailAndDui(email, dui);

        if(user == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("User not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        PresentationUserDetailsDTO userP = new PresentationUserDetailsDTO();
        userP.setId(user.getId());
        userP.setName(user.getName());
        userP.setEmail(user.getEmail());
        userP.setPhoneNumber(user.getPhone());
        userP.setRoles(user.getRolId());

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("User obtained successfully")
                        .data(userP)
                        .build(),
                HttpStatus.OK
        );
    }

    // DASHBOARD SECTION --------------------------------------------------------------

    @PreAuthorize("hasAuthority('Administrador')")
    @GetMapping("/dashboard")
    public ResponseEntity<GeneralResponse> getDashboard() {

        DashboardAdmDTO dashboard = new DashboardAdmDTO();

        // envia las entradas para poder hacer las graficas, seteados con el formato necesario
        List<PresentationEntryDetailsDTO> entries = entryService.getAllEntries()
                .stream().sorted(Comparator.comparing(Entry::getEntryDate).reversed()).map(entry -> {
                    PresentationEntryDetailsDTO entryDTO = new PresentationEntryDetailsDTO();
                    entryDTO.setId(entry.getId());
                    entryDTO.setDate(entry.getEntryDate());

                    // validar si el permiso existe
                    if(entry.getPermissionId() != null) {
                        // si el permiso existe, entonces se obtiene el tipo de entrada del permiso
                        entryDTO.setEntryType(permissionService.getPermission(entry.getPermissionId().getId()).getType());

                        // se obtiene el usuario y la casa del permiso con el formato necesario
                        PresentationUserDetailsDTO user = new PresentationUserDetailsDTO();
                        user.setId(userService.getUser(permissionService.getPermission(entry.getPermissionId().getId()).getUserId().getId()).getId());
                        user.setName(userService.getUser(permissionService.getPermission(entry.getPermissionId().getId()).getUserId().getId()).getName());
                        user.setEmail(userService.getUser(permissionService.getPermission(entry.getPermissionId().getId()).getUserId().getId()).getEmail());
                        user.setPhoneNumber(userService.getUser(permissionService.getPermission(entry.getPermissionId().getId()).getUserId().getId()).getPhone());
                        user.setRoles(userService.getUser(permissionService.getPermission(entry.getPermissionId().getId()).getUserId().getId()).getRolId());
                        entryDTO.setUser(user);

                        PresentationHomeDTO home = new PresentationHomeDTO();
                        home.setId(homeService.getHome(permissionService.getPermission(entry.getPermissionId().getId()).getHomeId().getId()).getId());
                        home.setHomeNumber(homeService.getHome(permissionService.getPermission(entry.getPermissionId().getId()).getHomeId().getId()).getHomeNumber());
                        home.setHomeBoss(homeService.getHome(permissionService.getPermission(entry.getPermissionId().getId()).getHomeId().getId()).getHomeOwnerId().getName());
                        entryDTO.setHome(home);
                    } else {
                        // si el permiso no existe, entonces la entrada es anonima
                        entryDTO.setUser(null);
                        entryDTO.setEntryType("Anonima");
                        entryDTO.setHome(null);// no hay permiso, por tanto es anonima
                    }

                    return entryDTO;
                }).toList();

        dashboard.setEntries(entries);

        // contar cuantos casas hay
        dashboard.setTotalHomes(homeService.getAllHomes().size());

        // contar cuantos residentes hay
        Role residentRole = roleService.getRoleByName("Residente");
        dashboard.setTotalResidents(userService.getAllUsersByRole(residentRole).size());

        // contar cuantos visitantes de hoy hay, contando cuantas entradas hubo el dia de hoy
        // Obtén la fecha actual como LocalDate
        LocalDate localDate = LocalDate.now();

        dashboard.setTotalVisitorsToday(entryService.getAllEntriesByDate(localDate).size());

        // envia el dto con la data necesaria para la vista
        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Dashboard obtained successfully")
                        .data(dashboard)
                        .build(),
                HttpStatus.OK
        );
    }

    // HOME SECTION --------------------------------------------------------------

    @PreAuthorize("hasAuthority('Administrador')")
    @GetMapping("/homes")
    public ResponseEntity<GeneralResponse> getAllHomes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        //List<Home> homes = homeService.getAllHomes();
        Pageable pageable = PageRequest.of(page, size);
        Page<Home> homes = homeService.getAllHomes(pageable);

        // se mapea la lista de casas a un dto para no enviar la data sensible
        List<PresentationHomeDTO> homesDTO = homes
                .stream().map(home -> {
                    PresentationHomeDTO homeDTO = new PresentationHomeDTO();
                    homeDTO.setId(home.getId());
                    homeDTO.setHomeNumber(home.getHomeNumber());

                    if(home.getHomeOwnerId() == null){
                        homeDTO.setHomeBoss("Sin propietario asignado");
                    }else{
                        homeDTO.setHomeBoss(home.getHomeOwnerId().getName());
                    }

                    return homeDTO;
                }).toList();

        // enviar una response que contenga el numero total de paginas y la pagina
        Map<String, Object> response = new HashMap<>();
        response.put("homes", homesDTO);
        response.put("totalPages", homes.getTotalPages());

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Homes obtained successfully")
                        .data(response)
                        .build(),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasAnyAuthority('Administrador')")
    @GetMapping("/homes/{homeId}")
    public ResponseEntity<GeneralResponse> getHome(@PathVariable UUID homeId) {

        Home home = homeService.getHome(homeId);

        if(home == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Home not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

//        HomeFullDataDTO homeFullDataDTO = new HomeFullDataDTO();
//        homeFullDataDTO.setId(home.getId());
//        homeFullDataDTO.setHomeNumber(home.getHomeNumber());
//        homeFullDataDTO.setAddress(home.getAddress());
//        homeFullDataDTO.setMembersNumber(home.getMembersNumber());
//        homeFullDataDTO.setUserAdmin(home.getHomeOwnerId());
//        homeFullDataDTO.setHomeMembers(home.getHomeMemberId());

        PresentationHomeDetailDTO homeFullDataDTO = new PresentationHomeDetailDTO();
        homeFullDataDTO.setId(home.getId());
        homeFullDataDTO.setHomeNumber(home.getHomeNumber());
        homeFullDataDTO.setMembersNumber(home.getMembersNumber());

        // setear el jefe de la casa
        PresentationUserDetailsDTO homeBoss = new PresentationUserDetailsDTO();
        homeBoss.setId(home.getHomeOwnerId().getId());
        homeBoss.setName(home.getHomeOwnerId().getName());
        homeBoss.setEmail(home.getHomeOwnerId().getEmail());
        homeBoss.setPhoneNumber(home.getHomeOwnerId().getPhone());
        homeBoss.setRoles(home.getHomeOwnerId().getRolId());
        homeFullDataDTO.setHomeBoss(homeBoss);

        // setear los miembros de la casa
        List<PresentationUserDetailsDTO> homeMembers = new ArrayList<>();
        for(User member : home.getHomeMemberId()) {
            PresentationUserDetailsDTO memberDTO = new PresentationUserDetailsDTO();
            memberDTO.setId(member.getId());
            memberDTO.setName(member.getName());
            memberDTO.setEmail(member.getEmail());
            memberDTO.setPhoneNumber(member.getPhone());
            memberDTO.setRoles(member.getRolId());
            homeMembers.add(memberDTO);
        }
        homeFullDataDTO.setMembers(homeMembers);

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Home obtained successfully")
                        .data(homeFullDataDTO)
                        .build(),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasAuthority('Administrador')")
    @PostMapping("/homes/register")
    public ResponseEntity<GeneralResponse> registerHome(@RequestBody HomeRegisterDataDTO info) {

        Home home = homeService.findHomeByAddressAndHomeNumber(info.getAddress(), info.getHomeNumber());

        if(home != null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Home is already registered")
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }
        // validar si se va a crear la casa vacia por defecto
        if(info.getHomeMembers().isEmpty() && info.getUserAdmin() == null) {
            // Crear un nuevo HomeRegisterDTO y llenarlo con la información de HomeRegisterDataDTO y data quemada vacia
            HomeRegisterDTO homeRegisterDTO = new HomeRegisterDTO();
            homeRegisterDTO.setHomeNumber(info.getHomeNumber());
            homeRegisterDTO.setAddress(info.getAddress());
            homeRegisterDTO.setMembersNumber(info.getMembersNumber());
            homeRegisterDTO.setUserAdmin(null);
            homeRegisterDTO.setHomeMembers(List.of());

            // Guardar la nueva casa
            homeService.saveHome(homeRegisterDTO);

            // TERMINA PRUEBA -------------------------------------------

            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Blank home registered successfully")
                            .build(),
                    HttpStatus.CREATED
            );
        }
        // validar que el jefe y los miembros no pertenezcan a otra casa
        if(!userHomeTools.CheckUserHome(info.getUserAdmin(), info.getHomeMembers())){
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Some users are already in a home")
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }

        // PRUEBA ---------------------------------------------------

        // Obtener el usuario administrador y cambiar su rol a "Encargado" y "Residente
        User adminUser = userService.getUser(info.getUserAdmin());

        if(adminUser == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Admin user not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        Role adminRole = roleService.getRoleByName("Encargado");
        userService.addRoleToUser(adminUser, adminRole);
        // Agregar el segundo rol al encargado
        userService.addRoleToUser(adminUser, roleService.getRoleByName("Residente"));

        // Crear una lista para almacenar los miembros de la casa
        List<User> homeMembers = new ArrayList<>();

        // Para cada ID de usuario en la lista de miembros de la casa
        for(UUID memberId : info.getHomeMembers()) {
            // Obtener el usuario y cambiar su rol a "Residente"
            User memberUser = userService.getUser(memberId);

            if(memberUser == null) {
                return new ResponseEntity<>(
                        new GeneralResponse.Builder()
                                .message("Member user not found")
                                .build(),
                        HttpStatus.NOT_FOUND
                );
            }

            Role memberRole = roleService.getRoleByName("Residente");
            userService.addRoleToUser(memberUser, memberRole);

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
                        .message("Home registered successfully")
                        .build(),
                HttpStatus.CREATED
        );
    }

    @PreAuthorize("hasAnyAuthority('Administrador')")
    @PatchMapping("/homes/update/{homeId}")
    public ResponseEntity<GeneralResponse> updateHome(@PathVariable UUID homeId, @RequestBody HomeRegisterDataDTO info) {

        Home home = homeService.getHome(homeId);

        if(home == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Home not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        // PRUEBA ---------------------------------------------------

        // primero elimino el rol de los usuarios de la casa actualmente
        userService.deleteRoleToUser(home.getHomeOwnerId(), roleService.getRoleByName("Residente"));
        userService.deleteRoleToUser(home.getHomeOwnerId(), roleService.getRoleByName("Encargado"));
        for(User member : home.getHomeMemberId()){
            userService.deleteRoleToUser(member, roleService.getRoleByName("Residente"));
        }

        // Obtener el nuevo usuario administrador y cambiar su rol a "Encargado"
        User adminUser = userService.getUser(info.getUserAdmin());
        Role adminRole = roleService.getRoleByName("Encargado");
        userService.addRoleToUser(adminUser, adminRole);
        userService.addRoleToUser(adminUser, roleService.getRoleByName("Residente"));

        // Crear una lista para almacenar los nuevos miembros de la casa
        List<User> homeMembers = new ArrayList<>();

        // Para cada ID de usuario en la lista de miembros de la casa
        for(UUID memberId : info.getHomeMembers()) {
            // Obtener el usuario y cambiar su rol a "Residente"
            User memberUser = userService.getUser(memberId);
            Role memberRole = roleService.getRoleByName("Residente");
            userService.addRoleToUser(memberUser, memberRole);

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
                        .message("Home updated successfully")
                        .build(),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasAuthority('Administrador')")
    @PatchMapping("/homes/delete/{homeId}")
    public ResponseEntity<GeneralResponse> deleteHome(@PathVariable UUID homeId) {

        Home home = homeService.getHome(homeId);

        if(home == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Home not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }
        // eliminar los roles de los usuarios de la casa
        userService.deleteRoleToUser(home.getHomeOwnerId(), roleService.getRoleByName("Residente"));
        userService.deleteRoleToUser(home.getHomeOwnerId(), roleService.getRoleByName("Encargado"));

        for(User member : home.getHomeMemberId()){
            userService.deleteRoleToUser(member, roleService.getRoleByName("Residente"));
        }

        homeService.deleteHome(home);

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Home deleted successfully")
                        .build(),
                HttpStatus.OK
        );
    }

    // ENTRY SECTION --------------------------------------------------------------
    @PreAuthorize("hasAuthority('Administrador')")
    @GetMapping("/entries")
    public ResponseEntity<GeneralResponse> getAllEntries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        // se mapea la lista de entradas a un dto para no enviar la data sensible y dar formato

//        List<EntryBoardAdmDTO> entries = entryService.getAllEntries()
//                .stream().map(entry -> {
//                    EntryBoardAdmDTO entryBoardAdmDTO = new EntryBoardAdmDTO();
//                    entryBoardAdmDTO.setId(entry.getId());
//                    entryBoardAdmDTO.setDate(entry.getEntryDate());
//                    entryBoardAdmDTO.setUser(userService.getUser(permissionService.getPermission(entry.getPermissionId().getId()).getUserId().getId()).getName());
//                    entryBoardAdmDTO.setHomeNumber(homeService.getHome(permissionService.getPermission(entry.getPermissionId().getId()).getHomeId().getId()).getHomeNumber());
//                    entryBoardAdmDTO.setEntryType(terminalService.getTerminalById(entry.getTerminalId().getTerminalId()).getEntryType());
//                    return entryBoardAdmDTO;
//                }).toList();

        Pageable pageable = PageRequest.of(page, size);

        List<PresentationEntryDTO> entries = entryService.getAllEntries(pageable)
                .stream().map(entry -> {
                    PresentationEntryDTO entryDTO = new PresentationEntryDTO();
                    entryDTO.setId(entry.getId());
                    entryDTO.setDate(entry.getEntryDate());

                    // validar si el permiso existe
                    if(entry.getPermissionId() != null) {
                        entryDTO.setUser(userService.getUser(permissionService.getPermission(entry.getPermissionId().getId()).getUserId().getId()).getName());
                        entryDTO.setEntryType(entry.getPermissionId().getType());
                        entryDTO.setHomeNumber(homeService.getHome(permissionService.getPermission(entry.getPermissionId().getId()).getHomeId().getId()).getHomeNumber());
                    } else {
                        entryDTO.setUser("-");
                        entryDTO.setEntryType("Anonima");
                        entryDTO.setHomeNumber(0);// no hay permiso, por tanto es anonima
                    }

                    return entryDTO;
                }).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("entries", entries);
        response.put("totalPages", entryService.getAllEntries(pageable).getTotalPages());

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Entries obtained successfully")
                        .data(response)
                        .build(),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasAuthority('Administrador')")
    @GetMapping("/entries/{entryId}")
    public ResponseEntity<GeneralResponse> getEntry(@PathVariable UUID entryId) {

            EntryBoardAdmDTO entryFormat = new EntryBoardAdmDTO();

            Entry entry = entryService.getEntry(entryId);

            if(entry == null) {
                return new ResponseEntity<>(
                        new GeneralResponse.Builder()
                                .message("Entry not found")
                                .build(),
                        HttpStatus.NOT_FOUND
                );
            }

            // se mapea la entrada a un dto para no enviar la data sensible y dar formato

            entryFormat.setId(entry.getId());
            entryFormat.setDate(entry.getEntryDate());
            entryFormat.setComment(entry.getComment());
            entryFormat.setEntryTypeTerminal(terminalService.getTerminalById(entry.getTerminalId().getTerminalId()).getEntryType());

            // validar si el permiso existe
            if(entry.getPermissionId() != null) {
                entryFormat.setEntryType(permissionService.getPermission(entry.getPermissionId().getId()).getType());
                entryFormat.setUser(userService.getUser(permissionService.getPermission(entry.getPermissionId().getId()).getUserId().getId()).getName());
                entryFormat.setHomeNumber(homeService.getHome(permissionService.getPermission(entry.getPermissionId().getId()).getHomeId().getId()).getHomeNumber());
            } else {
                entryFormat.setEntryType("Anonima");
                entryFormat.setUser("-");
                entryFormat.setHomeNumber(0);// no hay permiso, por tanto es anonima
            }

            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Entry obtained successfully")
                            .data(entryFormat)
                            .build(),
                    HttpStatus.OK
            );
    }

    @PreAuthorize("hasAuthority('Administrador')")
    @DeleteMapping("/entries/delete/{entryId}")
    public ResponseEntity<GeneralResponse> deleteEntry(@PathVariable UUID entryId){

            Entry entry = entryService.getEntry(entryId);

            if(entry == null) {
                return new ResponseEntity<>(
                        new GeneralResponse.Builder()
                                .message("Entry not found")
                                .build(),
                        HttpStatus.NOT_FOUND
                );
            }

            entryService.deleteEntry(entryId);

            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Entry deleted successfully")
                            .build(),
                    HttpStatus.OK
            );
    }

    //------GUARD SECTTION-------------------------------------------------------------
    @PreAuthorize("hasAuthority('Administrador')")
    @PostMapping("/addGuard/{userId}")
    public ResponseEntity<GeneralResponse> addGuard(@PathVariable UUID userId){

        User user = userService.getUser(userId);

        if(user == null){
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("User not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }
        Role rol = roleService.getRoleByName("Vigilante");
        userService.addRoleToUser(user, rol);

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Guard added")
                        .build(),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasAuthority('Administrador')")
    @PatchMapping("/deleteRole")
    public ResponseEntity<GeneralResponse> deleteRole(@RequestBody RoleUpdateDTO data){

        User user = userService.getUser(data.getUserId());

        if(user == null){
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("User not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        // debe recibir el rol con el nombre correcto
        Role rolDel = roleService.getRoleByName(data.getRole());

        if(rolDel == null){
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Role not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        userService.deleteRoleToUser(user, rolDel);

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("User role deleted")
                        .build(),
                HttpStatus.OK
        );
    }

}
