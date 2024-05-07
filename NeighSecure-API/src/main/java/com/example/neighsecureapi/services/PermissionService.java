package com.example.neighsecureapi.services;

import com.example.neighsecureapi.domain.entities.Permission;

import java.util.List;

public interface PermissionService {

    // TODO: implementar dtos, pagination y parametros correctos
    // CRUD IMPLEMENTATION FOR PERMISSION ENTITY
    public void savePermission(String permissionName);
    // quiza no se usa
    // TODO: validar todos los delete, para ver si solo cambiar un estado adicional para no eliminar de la base de datos
    public void deletePermission(String permissionId);

    public void updatePermission(String permissionId);
    public Permission getPermission(String permissionName);
    public List<Permission> getAllPermissions();

    // END OF CRUD IMPLEMENTATION

    // ADDITIONAL METHODS

    // Obtener permisos individuales
    public Permission getPermissionByPendingStatus(String permissionName);
    public Permission getPermissionsByUser(String permissionId, String userId);

    // Cambiar estado de permisos, tanto de valides como de pendiente
    public void changePermissionStatus(String permissionId, String permissionStatus);
    public void changePermissionPendingStatus(String permissionId);

    // Validar vigencia de permisos
    public boolean validatePermission(String permissionId, String userId);

}
