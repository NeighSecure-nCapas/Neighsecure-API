package com.example.neighsecureapi.domain.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Permission {
    // TODO: agregar anotaciones de JPA y cambiar nombres de atributos segun diccionario de datos
    private String id;
    private String type;
    private Date startDate;
    private Date endDate;
    // TODO: validar la forma en que solo reciba horas y minutos
    private Date startTime;
    private Date endTime;
    private boolean status;
    private String keyId;
    private Date generationDate;
    private List<String> days;
    private String homeId;
    private String userId;
}
