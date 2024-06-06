package com.example.neighsecureapi.controllers;


import com.example.neighsecureapi.domain.dtos.GeneralResponse;
import com.example.neighsecureapi.domain.dtos.homeDTOs.AddMemberDTO;
import com.example.neighsecureapi.domain.entities.Home;
import com.example.neighsecureapi.domain.entities.User;
import com.example.neighsecureapi.services.HomeService;
import com.example.neighsecureapi.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/homeboss")
@CrossOrigin
@Slf4j
public class HomeBossController {

    private final HomeService homeService;
    private final UserService userService;

    public HomeBossController(HomeService homeService, UserService userService) {
        this.homeService = homeService;
        this.userService = userService;
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

        return new ResponseEntity<>(
                new GeneralResponse.Builder()
                        .message("Member added successfully")
                        .build(),
                HttpStatus.OK
        );
    }


}
