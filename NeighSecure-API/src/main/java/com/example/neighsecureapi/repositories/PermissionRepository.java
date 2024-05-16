package com.example.neighsecureapi.repositories;

import com.example.neighsecureapi.domain.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
}
