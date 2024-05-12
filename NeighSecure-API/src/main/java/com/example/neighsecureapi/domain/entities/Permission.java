package com.example.neighsecureapi.domain.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Permiso")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "permisoId")
    private String id;

    @Column(name = "tipoPermiso")
    private String type;

    @Column(name = "fechaInicio")
    private Date startDate;

    @Column(name = "fechaFin")
    private Date endDate;

    // TODO: validar la forma en que solo reciba horas y minutos
    @Column(name = "horaInicio")
    private Date startTime;

    @Column(name = "horaFin")
    private Date endTime;

    @Column(name = "estado")
    private boolean status;

    // TODO: agregar llaves foraneas respectivas
    @Column(name = "llaveId")
    private String keyId;

    @Column(name = "fechaGeneracion")
    private Date generationDate;

    // TODO: validar como hacer que reciba varios dias
    //@Column(name = "dias")
    //private List<String> days;

    @Column(name = "casaId") // TODO: LLAVE FORANEA
    private String homeId;

    @Column(name = "usuarioId") // TODO: LLAVE FORANEA
    private String userId;
}
