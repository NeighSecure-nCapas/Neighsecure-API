package com.example.neighsecureapi.domain.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Permiso")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "permisoId")
    private UUID id;

    @Column(name = "tipoPermiso")
    private String type; // Unica, Multiple, Anonima

    @Column(name = "fechaInicio")
    private Date startDate;

    @Column(name = "fechaFin")
    private Date endDate;

    // TODO: validar la forma en que solo reciba horas y minutos
    @Column(name = "horaInicio")
    private Date startTime;

    @Column(name = "horaFin")
    private Date endTime;

    @Column(name = "estado")// aprobado true, rechazado false, pendiente null
    private Boolean status;

    @Column(name = "vigente")// aun es utilizable true, ya no false(y si no ha sido aprobado, no se puede usar)
    private boolean valid;

    // TODO: validar si esta bien la relacion
    @JoinColumn(name = "llaveId")
    @OneToOne
    private Key keyId;

    @Column(name = "fechaGeneracion")
    private Date generationDate;

    // TODO: validar como hacer que reciba varios dias
    @Column(name = "dias")
    private String days; // metodo del servicio para que reciba varios dias

    @JoinColumn(name = "casaId") // TODO: LLAVE FORANEA
    @ManyToOne(fetch = FetchType.LAZY)
    private Home homeId;

    @JoinColumn(name = "usuarioId") // TODO: LLAVE FORANEA
    @ManyToOne(fetch = FetchType.EAGER)
    //@JsonIgnore
    private User userId;// usuario al que pertenece el permiso

    @JoinColumn(name = "usuarioAuth") // TODO: LLAVE FORANEA
    @ManyToOne(fetch = FetchType.EAGER )
    //@JsonIgnore
    private User userAuth; // usuario que emitio el permiso

    @Column(name = "activo")
    private boolean active;// si el permiso sigue activo o no, para eliminarlo

    @OneToMany(mappedBy = "id", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Entry> entries;
}
