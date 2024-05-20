package com.example.neighsecureapi.services;

import com.example.neighsecureapi.domain.entities.Role;

import java.util.List;
import java.util.UUID;

public interface RoleService {

    // CRUD IMPLEMENTATION FOR ROLE ENTITY

    // es tabla catalogo, por tanto no se necesita crear o eliminar, solo leer

    // public void saveRole(String roleName);
    // public void deleteRole(String roleId);
    // public void updateRole(String roleName);
    public Role getRole(UUID roleId);
    public List<Role> getAllRoles();

    // END OF CRUD IMPLEMENTATION ---------------------------------------------------------------------

    // ADDITIONAL METHODS
    // public void addRoleToUser(String userId, String roleId);
    // public void removeRoleFromUser(String userId, String roleId);

    // obtener el rol por id de usuario
    // public Role getRoleByUser(String userId);
}
