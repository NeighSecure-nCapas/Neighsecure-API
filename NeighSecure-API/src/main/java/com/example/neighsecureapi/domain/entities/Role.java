package com.example.neighsecureapi.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @ManyToMany(mappedBy = "rolId")
    @JsonIgnore
    private List<User> users;
}
