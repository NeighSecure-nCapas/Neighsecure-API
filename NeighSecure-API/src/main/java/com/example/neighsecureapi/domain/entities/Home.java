package com.example.neighsecureapi.domain.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Casa")
public class Home {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "casaId")
    private UUID id;

    @Column(name = "numeroCasa")
    private int homeNumber;

    @Column(name = "direccion")
    private String address;

    // TODO: validar si es correcto, ya q no me deja hacer la relacion con el id correcto
    @JoinColumn(name = "usuarioIdEncargado")
    @JsonBackReference
    @OneToOne(fetch = FetchType.EAGER)
    //@JsonIgnore
    private User homeOwnerId;

    // TODO: validar si es correcto
    @Column(name = "usuarioIdHabitante", nullable = true)
    @JsonBackReference
    @OneToMany(fetch = FetchType.EAGER)// mappedBy = "homeId",
    @JsonIgnore
    private List<User> homeMemberId;

    @JsonBackReference
    @OneToMany(mappedBy = "homeId", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Permission> permissions;

    @Column(name = "estadoCasa")
    private boolean status;

    @Column(name = "numeroMiembros")
    private int membersNumber;
}
