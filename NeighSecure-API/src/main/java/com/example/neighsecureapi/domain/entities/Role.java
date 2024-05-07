package com.example.neighsecureapi.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO: agregar anotaciones de JPA
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    // TODO: agregar anotaciones de JPA y cambiar nombres de atributos segun diccionario de datos
    private String rolId;
    private String rol;
}
