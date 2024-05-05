package com.example.neighsecureapi.domain.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Home {
    private String id;
    private int homeNumber;
    private String address;
    private String homeOwnerId;
    private List<String> homeMemberId;
}
