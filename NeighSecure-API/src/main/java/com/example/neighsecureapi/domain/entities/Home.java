package com.example.neighsecureapi.domain.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
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

    // TODO: validar si es correcto, ya q no me deja hacer la relacion con el id correcto
    @Column(name = "usuarioIdEncargado")
    @OneToOne(mappedBy = "homeid", fetch = FetchType.LAZY)
    @JsonIgnore
    private User homeOwnerId;

    // TODO: validar si es correcto
    @Column(name = "usuarioIdHabitante", nullable = true)
    @OneToMany(mappedBy = "homeId", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<User> homeMemberId;

    @OneToMany(mappedBy = "homeId", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Permission> permissions;
}
