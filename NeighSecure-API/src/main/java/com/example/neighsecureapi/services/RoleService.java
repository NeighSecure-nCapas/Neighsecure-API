package com.example.neighsecureapi.services;

import com.example.neighsecureapi.domain.entities.Role;

import java.util.List;

public interface RoleService {

    // CRUD IMPLEMENTATION FOR ROLE ENTITY
    public void saveRole(String roleName);
    public void deleteRole(String roleId);
    public void updateRole(String roleName);
    public Role getRole(String roleName);
    public List<Role> getAllRoles();

    // END OF CRUD IMPLEMENTATION ---------------------------------------------------------------------

    // ADDITIONAL METHODS
    public void addRoleToUser(String userId, String roleId);
    public void removeRoleFromUser(String userId, String roleId);

    // obtener el rol por id de usuario
    public Role getRoleByUser(String userId);
}
