package com.example.neighsecureapi.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entry {
    // TODO: agregar anotaciones de JPA y cambiar nombres de atributos segun diccionario de datos
    private String id;
    private Date entryDate;
    private String terminalId;
    private String permissionId;
    private String comment;
}
