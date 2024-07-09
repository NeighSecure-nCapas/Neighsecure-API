package com.example.neighsecureapi.controllers;

import com.example.neighsecureapi.domain.dtos.GeneralResponse;
import com.example.neighsecureapi.domain.dtos.entryDTOs.EntryRegisterDTO;
import com.example.neighsecureapi.domain.dtos.entryDTOs.ObtainEntryDTO;
import com.example.neighsecureapi.domain.dtos.permissionDTOs.PermissionDTO;
import com.example.neighsecureapi.domain.entities.Entry;
import com.example.neighsecureapi.domain.entities.Key;
import com.example.neighsecureapi.domain.entities.Permission;
import com.example.neighsecureapi.domain.entities.Terminal;
import com.example.neighsecureapi.services.*;
import com.example.neighsecureapi.utils.servo.ServoTools;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/neighSecure/guard")
@Slf4j
public class GuardController {

    private final UserService userService;
    private final RoleService roleService;
    private final KeyService keyService;
    private final PermissionService permissionService;
    private final TerminalService terminalService;
    private final EntryService entryService;
    private final ServoTools servoTools;

    public GuardController(UserService userService, RoleService roleService, KeyService keyService, PermissionService permissionService, TerminalService terminalService, EntryService entryService, ServoTools servoTools) {
        this.userService = userService;
        this.roleService = roleService;
        this.keyService = keyService;
        this.permissionService = permissionService;
        this.terminalService = terminalService;
        this.entryService = entryService;
        this.servoTools = servoTools;
    }

    @PreAuthorize("hasAuthority('Vigilante')")
    @PostMapping("/anonymousEntry")
    public ResponseEntity<GeneralResponse> anonymousEntry(@RequestBody @Valid ObtainEntryDTO data) {

        // validar que la terminal existe
        Terminal terminal = terminalService.getTerminalById(data.getTerminalId());

        if (terminal == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Terminal not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        // el permiso no se valida porque es nulo al ser entrada anonima, pero se genera uno con tipo de entrada Anonima
        //Permission permission = new Permission(UUID.randomUUID(), "Anonima",
                //null, null, null, null, null,
                //true, null, Date.from(Instant.now()), null, null,
                //null, null, true, null);

        // guardo el permiso creado
        //permissionService.saveCreatedPermission(permission);


        // guardar la entrada anonima
        EntryRegisterDTO entryRegisterDTO = new EntryRegisterDTO();
        entryRegisterDTO.setDateAndHour(data.getDateAndHour());
        entryRegisterDTO.setComment(data.getComment());

        // abrir la puerta dependiendo de la terminal
        Map<String, Object> payload = Map.of("value", "ON");

        if(terminal.getEntryType().equals("Peatonal")){
            servoTools.movePea(payload);
        } else {
            servoTools.moveServo(payload);
        }

        // envio permiso null por q al ser anonima no tiene permiso relacionado
        entryService.saveEntry(entryRegisterDTO, terminal, null);

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Anonymous entry registered")
                        .build(),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasAuthority('Vigilante')")
    @PostMapping("/entry")
    public ResponseEntity<GeneralResponse> entry(@RequestBody @Valid ObtainEntryDTO data) {

        // validar que la terminal existe
        Terminal terminal = terminalService.getTerminalById(data.getTerminalId());

        if (terminal == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Terminal not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        // validar el rol del usuario
        if(!data.getRole().equals("Visitante")){ // si el rol es diferente a visitante, no tiene q validarse para q entre

            // TODO: enviar para q habra la puerta

            // registrar la entrada
            EntryRegisterDTO entryRegisterDTO = new EntryRegisterDTO();
            entryRegisterDTO.setDateAndHour(data.getDateAndHour());
            entryRegisterDTO.setComment(data.getComment());

            // abrir la puerta dependiendo de la terminal
            Map<String, Object> payload = Map.of("value", "ON");

            if(terminal.getEntryType().equals("Peatonal")){
                servoTools.movePea(payload);
            } else {
                servoTools.moveServo(payload);
            }

            entryService.saveEntry(entryRegisterDTO, terminal, permissionService.findPermissionByKeyId(keyService.getKey(data.getKeyId())));

            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Resident allowed")
                            .build(),
                    HttpStatus.OK
            );
        }


        // validar si la llave aun es valida
        Key key = keyService.getKey(data.getKeyId());

        if(!keyService.keyIsStillValid(key)){
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Key is no longer valid")
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }

        // validar que el permiso existe
        Permission permission = permissionService.findPermissionByKeyId(key);

        if (permission == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Permission not found")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }
        // validar si el permiso aun es valido
        if(!permission.isValid()){
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Permission is no longer valid")
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }

        // validar que el permiso este aprobado
        if (permission.getStatus() == null) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Permission is pending")
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!permission.getStatus()) {
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Permission is rejected")
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }
        // todo: agregar los 30 minutos de gracia para la validacion
        // validar que el permiso siga siendo valido, si la fecha de key esta dentro del rango de tiempo de permission
        if(!permissionService.validatePermission(permission, key)){
            // si ya no es valido, se cambia el valid del permiso
            permissionService.changePermissionValidationStatus(permission, false);
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Permission is no longer valid")
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        } else if (!permissionService.validateDayOfPermission(permission, key)) {// la fecha de la key si esta dentro del rango de permission, validar si el dia de la semana esta dentro de los dias del permiso

            // si no se encuentra el dia, entonces no tiene permiso de entrar ese dia
            return new ResponseEntity<>(
                    new GeneralResponse.Builder()
                            .message("Invalid day to enter")
                            .build(),
                    HttpStatus.BAD_REQUEST
            );

        }

        // si el permiso si es valido, se guarda la entrada

        EntryRegisterDTO entryRegisterDTO = new EntryRegisterDTO();
        entryRegisterDTO.setDateAndHour(data.getDateAndHour());
        entryRegisterDTO.setComment(data.getComment());

        // abrir la puerta dependiendo de la terminal
        Map<String, Object> payload = Map.of("value", "ON");

        if(terminal.getEntryType().equals("Peatonal")){
            servoTools.movePea(payload);
        } else {
            servoTools.moveServo(payload);
        }

        entryService.saveEntry(entryRegisterDTO, terminal, permission);

        // se valida el tipo de permiso para saber si se debe cambiar el estado del permiso
        if(permission.getType().equals("Unica")){
            // si es entrada Unica, entonces el permiso deja de ser valido una vez se registro la entrada
            permissionService.changePermissionValidationStatus(permission, false);
        }

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Entry registered")
                        .data(permission)// devuelvo el permiso para que el guardia pueda ver la info del permiso
                        .build(),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasAuthority('Vigilante')")
    @GetMapping("/terminal")
    public ResponseEntity<GeneralResponse> getTerminals() {
        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Terminals obtained")
                        .data(terminalService.getAllTerminals())
                        .build(),
                HttpStatus.OK
        );
    }

}
