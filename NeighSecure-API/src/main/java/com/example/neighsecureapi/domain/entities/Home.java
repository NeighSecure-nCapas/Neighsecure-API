package com.example.neighsecureapi.domain.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Home {
    // TODO: agregar anotaciones de JPA y cambiar nombres de atributos segun diccionario de datos
    private String id;
    private int homeNumber;
    private String address;
    private String homeOwnerId;
    private List<String> homeMemberId;
}
