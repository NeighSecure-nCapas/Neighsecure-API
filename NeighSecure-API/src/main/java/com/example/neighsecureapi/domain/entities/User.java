package com.example.neighsecureapi.domain.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonManagedReference
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "usuario_rol_id",
            joinColumns = @JoinColumn(name = "users_usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id_rol_id"))
    //@JsonIgnore
    private List<Role> rolId;

    /*
    @JoinColumn(name = "casaId", nullable = true)
    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    //@JsonIgnore
    private Home homeId;
    * */

    @Column(name = "dui")
    private String dui;

    @Column(name = "estadoUser")
    private boolean status;

    @JsonManagedReference
    @OneToMany(mappedBy = "userId", fetch = FetchType.LAZY)
    //@JsonIgnore
    private List<Permission> permissions;
}
