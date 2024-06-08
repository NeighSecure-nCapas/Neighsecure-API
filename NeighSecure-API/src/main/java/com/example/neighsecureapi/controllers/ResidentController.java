package com.example.neighsecureapi.controllers;

import com.example.neighsecureapi.domain.dtos.GeneralResponse;
import com.example.neighsecureapi.domain.dtos.permissionDTOs.PermissionDTO;
import com.example.neighsecureapi.domain.dtos.permissionDTOs.RegisterPermissionDTO;
import com.example.neighsecureapi.domain.entities.Home;
import com.example.neighsecureapi.domain.entities.Key;
import com.example.neighsecureapi.domain.entities.Permission;
import com.example.neighsecureapi.domain.entities.User;
import com.example.neighsecureapi.services.HomeService;
import com.example.neighsecureapi.services.KeyService;
import com.example.neighsecureapi.services.PermissionService;
import com.example.neighsecureapi.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
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

    public ResidentController(HomeService homeService, PermissionService permissionService, UserService userService, KeyService keyService) {
        this.homeService = homeService;
        this.permissionService = permissionService;
        this.userService = userService;
        this.keyService = keyService;
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

        if(info.getStartDate().before(Date.from(Instant.now()))) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("The start date must be greater than the current date")
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }

        if(info.getEndDate().before(Date.from(Instant.now()))) {
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

}
