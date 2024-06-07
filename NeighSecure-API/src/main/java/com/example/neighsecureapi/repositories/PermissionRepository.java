package com.example.neighsecureapi.repositories;

import com.example.neighsecureapi.domain.entities.Home;
import com.example.neighsecureapi.domain.entities.Key;
import com.example.neighsecureapi.domain.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    Optional<Permission> findByKeyIdAndActiveIsTrue(Key keyId);
    Optional<List<Permission>> findByHomeIdAndActiveIsTrue(Home homeId);
    List<Permission> findAllByActiveIsTrue();
}
