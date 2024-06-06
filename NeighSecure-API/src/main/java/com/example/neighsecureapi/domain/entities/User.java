package com.example.neighsecureapi.domain.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Usuario")
public class User implements UserDetails {
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
    @Fetch(FetchMode.JOIN)// esto es como hacer fetch en eager
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
    private Boolean active;

    @JsonManagedReference
    @OneToMany(mappedBy = "userId", fetch = FetchType.LAZY)
    //@JsonIgnore
    private List<Permission> permissions;

    // jwt things
    // TODO: ver si hay q cambiar todas las cuestiones afines a la variable status
    //@Column(name = "active", insertable = false)
    //private Boolean active;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Token> tokens;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.rolId.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRol()))
                .toList();
    }

    //getUsername is already overridden

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return this.active;
    }

    // TODO: ver como quitar esto
    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return this.name;
    }
    // -------------------------------
}
