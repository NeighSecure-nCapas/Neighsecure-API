package com.example.neighsecureapi.controllers;


import com.example.neighsecureapi.domain.dtos.GeneralResponse;
import com.example.neighsecureapi.domain.dtos.homeDTOs.AddMemberDTO;
import com.example.neighsecureapi.domain.dtos.permissionDTOs.PresentationPermissionDTO;
import com.example.neighsecureapi.domain.dtos.userDTOs.RegisterUserDTO;
import com.example.neighsecureapi.domain.dtos.userDTOs.UserResponseDTO;
import com.example.neighsecureapi.domain.entities.Home;
import com.example.neighsecureapi.domain.entities.Permission;
import com.example.neighsecureapi.domain.entities.Role;
import com.example.neighsecureapi.domain.entities.User;
import com.example.neighsecureapi.services.HomeService;
import com.example.neighsecureapi.services.PermissionService;
import com.example.neighsecureapi.services.RoleService;
import com.example.neighsecureapi.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/neighSecure/homeboss")
@CrossOrigin
@Slf4j
public class HomeBossController {

    private final HomeService homeService;
    private final UserService userService;
    private final RoleService roleService;
    private final PermissionService permissionService;

    public HomeBossController(HomeService homeService, UserService userService, RoleService roleService, PermissionService permissionService) {
        this.homeService = homeService;
        this.userService = userService;
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    // GESTION DE HOGAR------------------------------------------------------------------

    @PreAuthorize("hasAuthority('Encargado')")
    @GetMapping("/homeMembers/{homeId}")
    public ResponseEntity<GeneralResponse> getHomeMembers(@PathVariable UUID homeId) {

        Home home = homeService.getHome(homeId);

        if(home == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Home not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        List<User> members = home.getHomeMemberId();

        // implementar dto de presentacion
        List<RegisterUserDTO> membersDTO = members.stream().map(member -> {
            RegisterUserDTO memberDTO = new RegisterUserDTO();
            memberDTO.setName(member.getName());
            memberDTO.setEmail(member.getEmail());
            memberDTO.setDui(member.getDui());
            memberDTO.setPhone(member.getPhone());
            return memberDTO;
        }).toList();

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Members obtained successfully")
                        .data(membersDTO)
                        .build(),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasAuthority('Encargado')")
    @PostMapping("/addMember")
    public ResponseEntity<GeneralResponse> addMember(@RequestBody @Valid AddMemberDTO addMemberDTO) {

        Home home = homeService.getHome(addMemberDTO.getHomeId());

        if(home == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Home not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        // busco al nuevo miembro
        User user = userService.findUserByEmail(addMemberDTO.getUserEmail());

        if(user == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("User not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        // validar si la casa ya esta llena
        if(home.getMembersNumber() == home.getHomeMemberId().size()) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Home is full")
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }


        homeService.addHomeMembers(home, user);

        // agregar el rol al usuario
        Role rol = roleService.getRoleByName("Residente");
        userService.addRoleToUser(user, rol);

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Member added successfully")
                        .build(),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasAuthority('Encargado')")
    @PatchMapping("/removeMember")
    public ResponseEntity<GeneralResponse> removeMember(@RequestBody @Valid AddMemberDTO addMemberDTO) {

        Home home = homeService.getHome(addMemberDTO.getHomeId());

        if(home == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Home not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        // busco al usuario a eliminar de la casa
        User user = userService.findUserByEmail(addMemberDTO.getUserEmail());

        if(user == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("User not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        homeService.removeHomeMembers(home, user);

        // quitar el rol al usuario
        Role rol = roleService.getRoleByName("Residente");
        userService.deleteRoleToUser(user, rol);

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Member removed successfully")
                        .build(),
                HttpStatus.OK
        );
    }

    // GESTION DE VISITAS------------------------------------------------------------------

    // <OBTENER TODOS LOS PERMISOS DE LA CASA EN CONTROLADOR DE RESIDENTE>

    @PreAuthorize("hasAnyAuthority('Encargado', 'Residente')")
    @GetMapping("/permissions/{permissionId}")
    public ResponseEntity<GeneralResponse> getPermission(@PathVariable UUID permissionId) {

        Permission permission = permissionService.getPermission(permissionId);

        if(permission == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Permission not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        if(!permission.isActive()){
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Permission is not active")
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }

        // implementar dto de presentacion
        PresentationPermissionDTO permissionDTO = new PresentationPermissionDTO();
        permissionDTO.setId(permission.getId());
        permissionDTO.setType(permission.getType());
        permissionDTO.setStartDate(permission.getStartDate());
        permissionDTO.setEndDate(permission.getEndDate());
        permissionDTO.setStartTime(permission.getStartTime());
        permissionDTO.setEndTime(permission.getEndTime());
        permissionDTO.setGenerationDate(permission.getGenerationDate());
        permissionDTO.setDays(permission.getDays());
        permissionDTO.setHomeId(permission.getHomeId().getId());
        permissionDTO.setHomeNumber(permission.getHomeId().getHomeNumber());
        permissionDTO.setAddress(permission.getHomeId().getAddress());
        permissionDTO.setStatus(permission.getStatus());
        permissionDTO.setValid(permission.isValid());

        // generar dto de presentacion del userAuth
        UserResponseDTO userAuthDTO = new UserResponseDTO();
        userAuthDTO.setId(permission.getUserAuth().getId());
        userAuthDTO.setName(permission.getUserAuth().getName());
        userAuthDTO.setDui(permission.getUserAuth().getDui());
        userAuthDTO.setEmail(permission.getUserAuth().getEmail());
        userAuthDTO.setPhone(permission.getUserAuth().getPhone());
        userAuthDTO.setHomeNumber(null);

        permissionDTO.setUserAuth(userAuthDTO);

        // generar dto de presentacion del userAssociated
        UserResponseDTO userAssociatedDTO = new UserResponseDTO();
        userAssociatedDTO.setId(permission.getUserId().getId());
        userAssociatedDTO.setName(permission.getUserId().getName());
        userAssociatedDTO.setDui(permission.getUserId().getDui());
        userAssociatedDTO.setEmail(permission.getUserId().getEmail());
        userAssociatedDTO.setPhone(permission.getUserId().getPhone());
        userAssociatedDTO.setHomeNumber(null);

        permissionDTO.setUserAssociated(userAssociatedDTO);


        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Permission obtained successfully")
                        .data(permissionDTO)
                        .build(),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasAuthority('Encargado')")
    @PatchMapping("/permissions/approve/{permissionId}")
    public ResponseEntity<GeneralResponse> approvePermission(@PathVariable UUID permissionId) {

        Permission permission = permissionService.getPermission(permissionId);

        if(permission == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Permission not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        if(!permission.isActive()){
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Permission is not active")
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }

        // envio el permiso y cambio el estado a aprobado
        permissionService.changePermissionPendingStatus(permission, true);
        // cambio el estado a valido
        permissionService.changePermissionValidationStatus(permission, true);

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Permission approved successfully")
                        .build(),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasAuthority('Encargado')")
    @PatchMapping("/permissions/reject/{permissionId}")
    public ResponseEntity<GeneralResponse> rejectPermission(@PathVariable UUID permissionId) {

        Permission permission = permissionService.getPermission(permissionId);

        if(permission == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Permission not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        if(!permission.isActive()){
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Permission is not active")
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }

        // cambiar estado a rechazado (false) y a invalido (false) y eliminarlo (active false)
        permissionService.changePermissionPendingStatus(permission, false);
        permissionService.changePermissionValidationStatus(permission, false);
        permissionService.deletePermission(permission);

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Permission rejected successfully")
                        .build(),
                HttpStatus.OK
        );
    }


}
