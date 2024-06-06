package com.example.neighsecureapi.services;

import com.example.neighsecureapi.domain.dtos.PermissionDTO;
import com.example.neighsecureapi.domain.entities.Home;
import com.example.neighsecureapi.domain.entities.Key;
import com.example.neighsecureapi.domain.entities.Permission;
import com.example.neighsecureapi.domain.entities.User;

import java.util.List;
import java.util.UUID;

public interface PermissionService {

    // CRUD IMPLEMENTATION FOR PERMISSION ENTITY
    public void savePermission(PermissionDTO info, Key llave, Home casa, User user); // hay q validar el rol q genera el permiso
    public void deletePermission(UUID permissionId);

    // public void updatePermission(String permissionId);
    public Permission getPermission(UUID permissionId);

    // TODO: implementar pagination
    public List<Permission> getAllPermissions();

    // END OF CRUD IMPLEMENTATION

    // ADDITIONAL METHODS

    // Obtener permisos por estado de pendiente o validado por el encargado
    // public List<Permission> getPermissionByPendingStatus(String permissionId);
    // public List<Permission> getPermissionsByUser(String permissionId, String userId);

    // Cambiar estado de un permiso, si sigue siendo vigente para su uso
    public void changePermissionValidationStatus(Permission permissionId);
    // cambia el estado de un permiso solicitado, lo valida el encargado si es un permiso valido o no
    public void changePermissionPendingStatus(Permission permissionId, boolean status);

    // Validar vigencia de permisos
    // TODO: validar la relacion entre llave y permiso, puede q el id del permiso vaya en la tabla de llave y no al revez
    public boolean validatePermission(Permission permission, Key key);

    public Permission findPermissionByKeyId(Key keyId);


    /*
    metodos para setear bien los datos del atributo days en Permission, para manejarlo
    * // Método para convertir la cadena de días a una lista de cadenas
    public List<String> getDaysList() {
        return Arrays.asList(this.days.split(","));
    }

    // Método para convertir la lista de días a una cadena separada por comas
    public void setDaysList(List<String> daysList) {
        this.days = String.join(",", daysList);
    }
    * */
}
