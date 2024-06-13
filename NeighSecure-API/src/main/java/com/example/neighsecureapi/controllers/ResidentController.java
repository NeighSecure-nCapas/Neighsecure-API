package com.example.neighsecureapi.controllers;

import com.example.neighsecureapi.domain.dtos.GeneralResponse;
import com.example.neighsecureapi.domain.dtos.permissionDTOs.PermissionDTO;
import com.example.neighsecureapi.domain.dtos.permissionDTOs.RegisterPermissionDTO;
import com.example.neighsecureapi.domain.entities.*;
import com.example.neighsecureapi.services.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/neighSecure/resident")
@Slf4j
public class ResidentController {

    private final HomeService homeService;
    private final PermissionService permissionService;
    private final UserService userService;
    private final KeyService keyService;
    private final TokenService tokenService;
    private final RoleService roleService;

    public ResidentController(HomeService homeService, PermissionService permissionService, UserService userService, KeyService keyService, TokenService tokenService, RoleService roleService) {
        this.homeService = homeService;
        this.permissionService = permissionService;
        this.userService = userService;
        this.keyService = keyService;
        this.tokenService = tokenService;
        this.roleService = roleService;
    }

    @PreAuthorize("hasAnyAuthority('Encargado', 'Residente')")
    @GetMapping("/permissions/home/{homeId}")
    public ResponseEntity<GeneralResponse> getAllPermissionsByHome(@PathVariable UUID homeId) {

        Home home = homeService.getHome(homeId);

        if(home == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Home not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        List<Permission> permissions = permissionService.getPermissionsByHome(home);
        // TODO: validar si se debe retornar la lista de permisos entera o solo los que estan pendientes y aun son validos

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Permissions obtained successfully")
                        .data(permissions)
                        .build(),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasAnyAuthority('Encargado', 'Residente')")
    @PostMapping("/newPermission")
    public ResponseEntity<GeneralResponse> createPermission(@RequestBody @Valid RegisterPermissionDTO info) {

        User visitor = userService.getUser(info.getVisitor());

        if(visitor == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Visitor not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        User resident = userService.getUser(info.getGrantedBy());

        if(resident == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Resident not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        Home home = homeService.getHome(info.getHomeId());

        if(home == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Home not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }
        // validar que la casa pertenezca a quien emite el permiso
        // TODO: hacer funcion que valide si el residente pertenece a la casa
        if(!homeService.homeContainsUser(home, resident)) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("The home does not belong to the resident")
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }

        // validacion de las fechas, que todas sean mayores a la fecha actual
        //TODO: VALIDAR POR QUE RESTA 1 A LA FECHA RECIBIDA
        LocalDate startDate = LocalDate.ofInstant(info.getStartDate().toInstant(), ZoneId.systemDefault());
        LocalDate now = LocalDate.now();
        log.info("Fecha actual: " + now);
        log.info("Fecha de inicio: " + startDate);
        if(startDate.isBefore(now)) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("The start date must be greater than the current date")
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }

        LocalDate endDate = LocalDate.ofInstant(info.getEndDate().toInstant(), ZoneId.systemDefault());
        LocalDate nowEnd = LocalDate.now();

        if(endDate.isBefore(nowEnd) || endDate.isBefore(startDate)) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("The end date must be greater than the current date")
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }

        // creacion del objeto PermissionDTO para guardar el permiso

        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setType(info.getType());
        permissionDTO.setStartDate(info.getStartDate());
        permissionDTO.setEndDate(info.getEndDate());
        permissionDTO.setStartTime(info.getStartTime());
        permissionDTO.setEndTime(info.getEndTime());
        permissionDTO.setDays(info.getDays());
        permissionDTO.setGenerationDate(Date.from(Instant.now()));// guardar la fecha actual


        // validar el rol del usuario que genera el permiso para saber si se aprueba automaticamente o no
        // si resident contiene el rol "Encargado" se aprueba automaticamente

        if(resident.getRolId().stream().anyMatch(obj -> obj.getRol().equals("Encargado"))) {
            permissionDTO.setStatus(true);// aprobado
            permissionDTO.setValid(true);// valido
        } else {
            permissionDTO.setStatus(null);// sin aprobar, por tanto
            permissionDTO.setValid(false);// el permiso aun no es valido
        }

        // genero la llave vacia para que se pueda generar el permiso
        Key key = new Key();
        keyService.saveKey(key);

        permissionService.savePermission(permissionDTO, key, home, visitor, resident);

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Permission created successfully")
                        .build(),
                HttpStatus.CREATED
        );
    }

    @PreAuthorize("hasAnyAuthority('Encargado', 'Residente')")// TODO:  validar si solo el encargado puede eliminar permisos
    @PatchMapping("/permissions/delete/{permissionId}")
    public ResponseEntity<GeneralResponse> deletePermission(@RequestHeader("Authorization") String bearerToken, @PathVariable UUID permissionId) {

        String token = bearerToken.substring(7);
        Token tokenObj = tokenService.findTokenBycontent(token);

        if(tokenObj == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Token not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        User user = userService.findUserByToken(tokenObj);

        if(user == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("User not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

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

        // si el residente es Encargado, puede eliminar el permiso aunque no lo haya generado
        if(user.getRolId().contains(roleService.getRoleByName("Encargado"))){
            permissionService.deletePermission(permission);
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Permission deleted successfully by the manager")
                            .build(),
                    HttpStatus.OK
            );
        }

        // validar que el usuario sea quien genero el permiso
        if(!permission.getUserAuth().equals(user)) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("The permission does not belong to the user")
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }

        permissionService.deletePermission(permission);

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Permission deleted successfully by the resident")
                        .build(),
                HttpStatus.OK
        );
    }

}
