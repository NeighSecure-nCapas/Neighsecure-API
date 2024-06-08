package com.example.neighsecureapi.controllers;

import com.example.neighsecureapi.domain.dtos.GeneralResponse;
import com.example.neighsecureapi.domain.dtos.KeyUpdateDTO;
import com.example.neighsecureapi.domain.dtos.permissionDTOs.ValidatePermissionDTO;
import com.example.neighsecureapi.domain.entities.Key;
import com.example.neighsecureapi.domain.entities.Permission;
import com.example.neighsecureapi.domain.entities.Role;
import com.example.neighsecureapi.domain.entities.User;
import com.example.neighsecureapi.services.KeyService;
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
@RequestMapping("/neighSecure/visit")
@Slf4j
public class VisitController {

    private final UserService userService;
    private final PermissionService permissionService;
    private final KeyService keyService;
    private final RoleService roleService;

    public VisitController(UserService userService, PermissionService permissionService, KeyService keyService, RoleService roleService) {
        this.userService = userService;
        this.permissionService = permissionService;
        this.keyService = keyService;
        this.roleService = roleService;
    }

    @PreAuthorize("hasAnyAuthority('Visitante')")
    @GetMapping("/myPermissions/{userId}")
    public ResponseEntity<GeneralResponse> getMyPermissions(@PathVariable UUID userId) {

        // TODO: ver si solo mandar la data necesaria para mostrar en el permiso asi como el uuid
        User user = userService.getUser(userId);

        if(user == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("User not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        // Obtener los permisos del usuario
        // la funcion ya devuelve los permisos aprobados y que aun son validos
        List<Permission> permissions = permissionService.getPermissionsByUser(user);

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Permissions obtained successfully")
                        .data(permissions)
                        .build(),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasAnyAuthority('Visitante', 'Encargado', 'Residente')")// al momento que se le da generarQR
    @PostMapping("/validatePermission")
    public ResponseEntity<GeneralResponse> validatePermission(@RequestBody @Valid ValidatePermissionDTO Data) {

        Role rolUser = roleService.getRoleByName(Data.getRole());

        if(rolUser == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Role not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        // si el rol es de visitante
        if(rolUser.getRol().equals("Visitante")) {

            Permission permission = permissionService.getPermission(Data.getPermissionId());

            if(permission == null) {
                return new ResponseEntity<>(
                        new GeneralResponse.Builder()
                                .message("Permission not found")
                                .build(),
                        HttpStatus.NOT_FOUND
                );
            }

            // busco la llave que corresponde al permiso para actualizar sus campos
            Key key = permission.getKeyId();

            KeyUpdateDTO keyData = new KeyUpdateDTO();
            keyData.setGenerationDate(Data.getGenerationDate());
            keyData.setGenerationTime(Data.getGenerationTime());
            keyData.setGenerationDay(Data.getGenerationDay());

            keyService.updateKey(key, keyData);// el dto trae la informacion del momento en que se hizo la peticion

            // valido el permiso con respecto de la llave

            // si la llave no es valida, se cambia el valor de valid y active del permiso a false
            if(!permissionService.validatePermission(permission, key)) {

                permissionService.changePermissionValidationStatus(permission, false);
                permissionService.deletePermission(permission);

                return new ResponseEntity<>(
                        new GeneralResponse.Builder()
                                .message("Permission is not valid anymore")// el permiso ya no es valido y deberia refrescarse la data en front
                                .build(),
                        HttpStatus.BAD_REQUEST
                );
            }

            /*
            * El QR debe generarse a partir de los campos de la llave y el nombre del rol
            * */
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Permission is still valid for visitor")
                            .data(key)// retorno la llave para que se genere el QR
                            .build(),
                    HttpStatus.OK
            );

        }

        // si el rol es de encargado o residente se crea una nueva llave para generar el permiso
        // el QR debe generarse con el id de la llave y el id del rol

        // se crea una nueva llave
        Key key = new Key();

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Permission valid for resident or manager")
                        .data(key)
                        .build(),
                HttpStatus.OK
        );
    }

}
