package com.example.neighsecureapi.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Terminal {
    // TODO: agregar anotaciones de JPA y cambiar nombres de atributos segun diccionario de datos
    private String terminalId;
    private String entryType; // podria ser un enum (? peatonal o vehicular
}
