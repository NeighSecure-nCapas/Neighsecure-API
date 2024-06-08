package com.example.neighsecureapi.controllers;

import com.example.neighsecureapi.domain.dtos.GeneralResponse;
import com.example.neighsecureapi.domain.dtos.entryDTOs.EntryRegisterDTO;
import com.example.neighsecureapi.domain.dtos.entryDTOs.ObtainEntryDTO;
import com.example.neighsecureapi.domain.dtos.permissionDTOs.PermissionDTO;
import com.example.neighsecureapi.domain.entities.Entry;
import com.example.neighsecureapi.domain.entities.Permission;
import com.example.neighsecureapi.domain.entities.Terminal;
import com.example.neighsecureapi.services.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Date;
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

    public GuardController(UserService userService, RoleService roleService, KeyService keyService, PermissionService permissionService, TerminalService terminalService, EntryService entryService) {
        this.userService = userService;
        this.roleService = roleService;
        this.keyService = keyService;
        this.permissionService = permissionService;
        this.terminalService = terminalService;
        this.entryService = entryService;
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
        Permission permission = new Permission(UUID.randomUUID(), "Anonima",
                null, null, null, null, null,
                true, null, Date.from(Instant.now()), null, null,
                null, null, true, null);

        // guardo el permiso creado
        permissionService.saveCreatedPermission(permission);


        // guardar la entrada anonima
        EntryRegisterDTO entryRegisterDTO = new EntryRegisterDTO();
        entryRegisterDTO.setDateAndHour(data.getDateAndHour());
        entryRegisterDTO.setComment(data.getComment());

        entryService.saveEntry(entryRegisterDTO, terminal, permission);

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Anonymous entry registered")
                        .build(),
                HttpStatus.OK
        );
    }

}
