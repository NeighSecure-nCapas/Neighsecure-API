package com.example.neighsecureapi.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Key {
    // TODO: agregar anotaciones de JPA y cambiar nombres de atributos segun diccionario de datos
    private String keyId;
    // TODO: validar formatos de fecha y horas
    private Date generationDate;
    private Date generationTime;
    private String generationDay;
}
