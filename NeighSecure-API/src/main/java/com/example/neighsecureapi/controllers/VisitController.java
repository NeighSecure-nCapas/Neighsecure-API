package com.example.neighsecureapi.controllers;

import com.example.neighsecureapi.domain.dtos.GeneralResponse;
import com.example.neighsecureapi.domain.dtos.KeyUpdateDTO;
import com.example.neighsecureapi.domain.dtos.permissionDTOs.PresentationPermissionDTO;
import com.example.neighsecureapi.domain.dtos.permissionDTOs.ValidatePermissionDTO;
import com.example.neighsecureapi.domain.dtos.userDTOs.RegisterDuiAndPhoneDTO;
import com.example.neighsecureapi.domain.dtos.userDTOs.UserResponseDTO;
import com.example.neighsecureapi.domain.entities.*;
import com.example.neighsecureapi.services.*;
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
    private final TokenService tokenService;
    private final HomeService homeService;

    public VisitController(UserService userService, PermissionService permissionService, KeyService keyService, RoleService roleService, TokenService tokenService, HomeService homeService) {
        this.userService = userService;
        this.permissionService = permissionService;
        this.keyService = keyService;
        this.roleService = roleService;
        this.tokenService = tokenService;
        this.homeService = homeService;
    }

    @PreAuthorize("hasAnyAuthority('Visitante')")
    @GetMapping("/myPermissions")
    public ResponseEntity<GeneralResponse> getMyPermissions(@RequestHeader("Authorization") String bearerToken) {

        String token = bearerToken.substring(7);
        Token tokenEntity = tokenService.findTokenBycontent(token);

        User user = userService.findUserByToken(tokenEntity);

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

        // implementar dtod e presentacion para retornar solo los datos necesarios
        List<PresentationPermissionDTO> permissionsDTO = permissions.stream()
                .filter(permission -> permission.getUserAuth() != null && permission.getStartDate() != null && permission.getEndDate() != null)
                .map(permission -> {
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

                    return permissionDTO;
                }).toList();

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Permissions obtained successfully")
                        .data(permissionsDTO)
                        .build(),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasAnyAuthority('Visitante', 'Encargado', 'Residente')")// al momento que se le da generarQR
    @PostMapping("/validatePermission")
    public ResponseEntity<GeneralResponse> validatePermission(@RequestHeader("Authorization") String bearerToken, @RequestBody @Valid ValidatePermissionDTO Data) {

        String token = bearerToken.substring(7);
        Token tokenEntity = tokenService.findTokenBycontent(token);

        User user = userService.findUserByToken(tokenEntity);

        if(user == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("User not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        // si el rol es de Residente
        Role residenteRol = roleService.getRoleByName("Residente");

        if(!user.getRolId().contains(residenteRol)) {

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
            UUID keyId = permission.getKeyId().getKeyId();
            Key key = keyService.getKey(keyId);

            KeyUpdateDTO keyData = new KeyUpdateDTO();
            keyData.setGenerationDate(Data.getGenerationDate());
            keyData.setGenerationTime(Data.getGenerationTime());
            keyData.setGenerationDay(Data.getGenerationDay());

            keyService.updateKey(key, keyData);// el dto trae la informacion del momento en que se hizo la peticion

            // obtengo la llave otra vez para tener los cambios actualizados
            Key keyUpdated = keyService.getKey(keyId);

            // valido el permiso con respecto de la llave

            // validar si la llave es apta para usarse en ese momento
            if(!permissionService.validatePermission(permission, key)) {

                // si no lo es, validar si el permiso ya no es valido o si se trata de una entrada en un momento no permitido
                if(!permissionService.validateTimeOfPermission(permission)) {

                    // si la llave no es valida, se cambia el valor de valid y active del permiso a false
                    permissionService.changePermissionValidationStatus(permission, false);
                    permissionService.deletePermission(permission);

                    return new ResponseEntity<>(
                            new GeneralResponse.Builder()
                                    .message("Permission is not valid anymore")// el permiso ya no es valido y deberia refrescarse la data en front
                                    .build(),
                            HttpStatus.BAD_REQUEST
                    );
                }
                // si no entra al if, es por que el permiso aun es valido pero quiere entrar en un momento no permitido
                return new ResponseEntity<>(
                        new GeneralResponse.Builder()
                                .message("Permission is valid but not in the right time")
                                .build(),
                        HttpStatus.BAD_REQUEST
                );

            }

            /*
            * El QR debe generarse a partir de los campos de la llave (contando id) y el nombre del rol
            * */

            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Permission is still valid for visitor")
                            .data(keyUpdated)// retorno la llave para que se genere el QR
                            .build(),
                    HttpStatus.OK
            );

        }

        // si el rol es de encargado o residente se crea una nueva llave para generar el permiso
        // el QR debe generarse con el id de la llave y el id del rol, y la data de la llave

        // se crea una nueva llave
        Key key = new Key();
        keyService.saveKey(key);
        // se crea un nuevo permiso valido para el residente junto a su llave
        Permission permission = new Permission();
        permission.setId(UUID.randomUUID());
        permission.setUserId(user);
        permission.setKeyId(key);
        permission.setHomeId(homeService.findHomeByUser(user));
        permission.setValid(true);
        permission.setActive(true);
        permission.setStatus(true);
        permission.setType("Residente");
        permissionService.saveCreatedPermission(permission);

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Permission valid for resident or manager")
                        .data(key)
                        .build(),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasAnyAuthority('Visitante')")
    @PostMapping("/completeRegister")
    public ResponseEntity<GeneralResponse> addDuiAndPhone(@RequestHeader("Authorization") String bearerToken, @RequestBody @Valid RegisterDuiAndPhoneDTO data){

        // buscar el usuario por el token
        String token = bearerToken.substring(7);
        Token tokenEntity = tokenService.findTokenBycontent(token);

        if(tokenEntity == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Token not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        User user = userService.findUserByToken(tokenEntity);

        if(user == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("User not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        // agregar el dui y el telefono al usuario
        userService.setDuiAndPhoneToUser(user, data.getDui(), data.getPhone());

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Dui and phone added successfully")
                        .build(),
                HttpStatus.OK
        );
    }

}
