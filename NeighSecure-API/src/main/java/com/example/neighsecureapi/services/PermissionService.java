package com.example.neighsecureapi.services;

import com.example.neighsecureapi.domain.entities.Permission;

import java.util.List;

public interface PermissionService {

    // TODO: implementar dtos, pagination y parametros correctos
    // CRUD IMPLEMENTATION FOR PERMISSION ENTITY
    public void savePermission(String permissionName); // hay q validar el rol q genera el permiso
    // quiza no se usa
    // TODO: validar todos los delete, para ver si solo cambiar un estado adicional para no eliminar de la base de datos
    public void deletePermission(String permissionId);

    public void updatePermission(String permissionId);
    public Permission getPermission(String permissionName);
    public List<Permission> getAllPermissions();

    // END OF CRUD IMPLEMENTATION

    // ADDITIONAL METHODS

    // Obtener permisos por estado de pendiente o validado por el encargado
    public List<Permission> getPermissionByPendingStatus(String permissionId);
    public List<Permission> getPermissionsByUser(String permissionId, String userId);

    // Cambiar estado de un permiso, si sigue siendo vigente para su uso
    public void changePermissionValidationStatus(String permissionId, String permissionStatus);
    // cambia el estado de un permiso solicitado, lo valida el encargado si es un permiso valido o no
    public void changePermissionPendingStatus(String permissionId);

    // Validar vigencia de permisos
    public boolean validatePermission(String permissionId, String userId);

}
