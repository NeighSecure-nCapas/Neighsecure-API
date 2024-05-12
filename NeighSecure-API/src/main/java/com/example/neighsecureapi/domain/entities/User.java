package com.example.neighsecureapi.domain.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Usuario")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "usuarioId")
    private String id;

    @Column(name = "nombreCompleto")
    private String name;

    @Column(name = "correo")
    private String email;

    @Column(name = "telefono")
    private String phone;

    // TODO: ver como agregar varios roles
    //@Column(name = "rolId")
    //private List<String> rolId;

    @Column(name = "casaId") // TODO: LLAVE FORANEA
    private String homeId;

    @Column(name = "dui")
    private String dui;
}
