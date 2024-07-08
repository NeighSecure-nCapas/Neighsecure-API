package com.example.neighsecureapi.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

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
    private UUID rolId;

    @Column(name = "rol")
    private String rol;

    @JsonBackReference
    @ManyToMany(mappedBy = "rolId")
    @JsonIgnore
    private List<User> users;
}
