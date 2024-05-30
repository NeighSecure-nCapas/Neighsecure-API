package com.example.neighsecureapi.repositories;

import com.example.neighsecureapi.domain.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Role findRoleByRol(String rol);
}
