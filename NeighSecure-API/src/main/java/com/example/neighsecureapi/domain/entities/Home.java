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
@Table(name = "Casa")
public class Home {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "casaId")
    private String id;

    @Column(name = "numeroCasa")
    private int homeNumber;

    @Column(name = "direccion")
    private String address;

    // TODO: agregar llaves foraneas respectivas
    @Column(name = "usuarioIdEncargado")
    private String homeOwnerId;

    // TODO: agregar llaves foraneas respectivas y ver como hacer para almacenar varios id de habitantes
    //@Column(name = "usuarioIdHabitante")
    //private List<String> homeMemberId;
}
