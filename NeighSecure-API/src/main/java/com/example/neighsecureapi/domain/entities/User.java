package com.example.neighsecureapi.domain.entities;


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
@Table(name = "Usuario")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "usuarioId")
    private UUID id;

    @Column(name = "nombreCompleto")
    private String name;

    @Column(name = "correo")
    private String email;

    @Column(name = "telefono")
    private String phone;

    // TODO: ver como agregar varios roles
    @Column(name = "rolId")
    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Role> rolId;

    @Column(name = "casaId", nullable = true) // TODO: validar si es correcto
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private Home homeId;

    @Column(name = "dui")
    private String dui;

    @Column(name = "estadoUser")
    private boolean status;

    @OneToMany(mappedBy = "userId", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Permission> permissions;
}
