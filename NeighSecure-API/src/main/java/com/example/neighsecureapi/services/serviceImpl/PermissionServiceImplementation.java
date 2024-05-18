package com.example.neighsecureapi.services.serviceImpl;

import com.example.neighsecureapi.domain.entities.Permission;
import com.example.neighsecureapi.repositories.PermissionRepository;
import com.example.neighsecureapi.services.PermissionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImplementation implements PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionServiceImplementation(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public void savePermission(String permissionName) {

    }

    @Override
    public void deletePermission(String permissionId) {

    }

    @Override
    public void updatePermission(String permissionId) {

    }

    @Override
    public Permission getPermission(String permissionName) {
        return null;
    }

    @Override
    public List<Permission> getAllPermissions() {
        return List.of();
    }

    @Override
    public List<Permission> getPermissionByPendingStatus(String permissionId) {
        return List.of();
    }

    @Override
    public List<Permission> getPermissionsByUser(String permissionId, String userId) {
        return List.of();
    }

    @Override
    public void changePermissionValidationStatus(String permissionId, String permissionStatus) {

    }

    @Override
    public void changePermissionPendingStatus(String permissionId) {

    }

    @Override
    public boolean validatePermission(String permissionId, String userId) {
        return false;
    }
}
