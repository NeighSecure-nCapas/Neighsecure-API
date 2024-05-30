package com.example.neighsecureapi.services.serviceImpl;

import com.example.neighsecureapi.domain.dtos.PermissionDTO;
import com.example.neighsecureapi.domain.entities.Home;
import com.example.neighsecureapi.domain.entities.Key;
import com.example.neighsecureapi.domain.entities.Permission;
import com.example.neighsecureapi.domain.entities.User;
import com.example.neighsecureapi.repositories.PermissionRepository;
import com.example.neighsecureapi.services.PermissionService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class PermissionServiceImplementation implements PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionServiceImplementation(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void savePermission(PermissionDTO info, Key key, Home casa, User user) {
        Permission permission = new Permission();

        permission.setType(info.getType());
        permission.setStartDate(info.getStartDate());
        permission.setEndDate(info.getEndDate());
        permission.setStartTime(info.getStartTime());
        permission.setEndTime(info.getEndTime());
        permission.setStatus(info.isStatus());
        permission.setKeyId(key);
        permission.setGenerationDate(info.getGenerationDate());
        permission.setDays(info.getDays());
        permission.setHomeId(casa);
        permission.setUserId(user);

        permissionRepository.save(permission);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void deletePermission(UUID permissionId) {
        permissionRepository.deleteById(permissionId);
    }

    @Override
    public Permission getPermission(UUID permissionId) {
        return permissionRepository.findById(permissionId).orElse(null);
    }

    @Override
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void changePermissionValidationStatus(Permission permission) {
        permission.setValid(false);
        permissionRepository.save(permission);

    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void changePermissionPendingStatus(Permission permission) {
        permission.setStatus(true);
        permissionRepository.save(permission);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public boolean validatePermission(Permission permission, Key key) {
        // validar a base de comparacion de parametros
        /*FUNCIONAMIENTO DE LA VALIDACION DE PERMISOS, PROCESO PARA LLEGAR AQUI
        1. El usuario solicita un permiso
        2. Al solicitarlo, se llama a savePermission, pero antes se CREA una llave con campos null, y a savePermission
        se le pasa el uuid de esa llave
        3. ya una vez aceptada la solicitud, el usuario tiene permitido el uso del permiso
        4. cuando el visitante da click al permiso para entrar, busca si existe un permiso con el id de la llave, o bueno
        la data ya esta directamente en el permiso al q se le dio click
        5. se busca la llave con el id del permiso, y se actualizan los datos referentes a tiempo
        6. se genera el qr en base al uuid de la llave q se saco del permiso
        -----LECTURA DEL QR-----
        7. al leer el qr, se saca el id de la llave
        8. se busca un permiso q contenga esa llave y que siga teniendo vigencia
        8.5. esta funcion ya recibe un permiso vigente y aprobado
        9. si se encuentra, se valida en base a los parametros del permiso y la llave
        10. si es valido, se permite el acceso y se actualiza el estado del permiso
        * */
        // TODO: validacion de parametros :b

        return false;
    }

    @Override
    public Permission findPermissionByKeyId(Key keyId) {
        return permissionRepository.findByKeyId(keyId).orElse(null);
    }
}
