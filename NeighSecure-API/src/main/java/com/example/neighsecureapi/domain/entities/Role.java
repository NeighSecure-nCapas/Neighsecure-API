package com.example.neighsecureapi.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO: agregar anotaciones de JPA
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Rol")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "rolId")
    private String rolId;

    @Column(name = "rol")
    private String rol;
}
