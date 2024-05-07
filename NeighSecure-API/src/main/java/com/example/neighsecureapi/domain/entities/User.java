package com.example.neighsecureapi.domain.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    // TODO: agregar anotaciones de JPA y cambiar nombres de atributos segun diccionario de datos
    private String id;
    private String name;
    private String email;
    private String phone;
    private List<String> rolId;
    private String homeId;
    private String dui;
}
