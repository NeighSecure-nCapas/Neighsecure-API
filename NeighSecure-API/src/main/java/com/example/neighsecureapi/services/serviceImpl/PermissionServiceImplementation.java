package com.example.neighsecureapi.services.serviceImpl;

import com.example.neighsecureapi.domain.dtos.permissionDTOs.PermissionDTO;
import com.example.neighsecureapi.domain.entities.Home;
import com.example.neighsecureapi.domain.entities.Key;
import com.example.neighsecureapi.domain.entities.Permission;
import com.example.neighsecureapi.domain.entities.User;
import com.example.neighsecureapi.repositories.PermissionRepository;
import com.example.neighsecureapi.services.PermissionService;
import com.example.neighsecureapi.utils.ArrayManagementTools;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PermissionServiceImplementation implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final ArrayManagementTools arrayManagementTools;

    public PermissionServiceImplementation(PermissionRepository permissionRepository, ArrayManagementTools arrayManagementTools) {
        this.permissionRepository = permissionRepository;
        this.arrayManagementTools = arrayManagementTools;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void savePermission(PermissionDTO info, Key key, Home casa, User user, User permisoGenerador) {
        Permission permission = new Permission();

        permission.setType(info.getType());
        permission.setStartDate(info.getStartDate());
        permission.setEndDate(info.getEndDate());
        permission.setStartTime(info.getStartTime());
        permission.setEndTime(info.getEndTime());
        permission.setStatus(info.getStatus()); // si lo genera el encargado, se pone en true, sino en null
        permission.setKeyId(key);
        permission.setGenerationDate(info.getGenerationDate());
        permission.setDays(info.getDays());
        permission.setHomeId(casa);
        permission.setUserId(user);
        permission.setUserAuth(permisoGenerador);
        permission.setValid(info.isValid());
        permission.setActive(true);


        permissionRepository.save(permission);
    }

    @Override
    public void saveCreatedPermission(Permission permission) {
        permissionRepository.save(permission);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void deletePermission(Permission permissionId) {

        permissionId.setActive(false);

        permissionRepository.save(permissionId);
    }

    @Override
    public Permission getPermission(UUID permissionId) {
        return permissionRepository.findById(permissionId).orElse(null);
    }

    @Override
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAllByActiveIsTrue();
    }

    @Override
    public List<Permission> getPermissionsByHome(Home homeId) {
        return permissionRepository.findByHomeIdAndActiveIsTrue(homeId).orElse(null);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void changePermissionValidationStatus(Permission permission, boolean status) {
        permission.setValid(status);
        permissionRepository.save(permission);

    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void changePermissionPendingStatus(Permission permission, boolean status) {
        permission.setStatus(status);
        permissionRepository.save(permission);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public boolean validatePermission(Permission permission, Key key) {// valida para el status en permiso
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

        /*
        1. cuando el visitante da click en generar qr, se busca la llave de ese permiso y se actualiza los campos
        2. ya con los nuevos campos, se valida si la data de tiempo que esta en la llave esta dentro de los parametros del permiso
        3. si es asi, se genera el qr mandando el uuid de la llave
        4. si no, se actualiza el campo de status del permiso a false por q ya no es utilizable

        CUANDO SE LEE EL QR
        1. se busca el permiso con el id de la llave
        2. se valida si la data de tiempo de la llave esta dentro de los parametros del permiso
        3. si es asi, se permite el acceso y se actualiza el campo de status del permiso a false si se trata de entrada unica
        3.1 si es un permiso de entrada multiple, se mantiene en true el status
        * */

        // VALIDAR  SI LA FECHA DE LA KEY ESTA DENTRO DE LAS FECHAS DEL PERMISO
        if(key.getGenerationDate().compareTo(permission.getStartDate()) >= 0
                && key.getGenerationDate().compareTo(permission.getEndDate()) <= 0) {
            // VALIDAR SI LA HORA DE LA KEY ESTA DENTRO DE LAS HORAS DEL PERMISO
            if (key.getGenerationTime().compareTo(permission.getStartTime()) >= 0
                    && key.getGenerationTime().compareTo(permission.getEndTime()) <= 0) {

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean validateDayOfPermission(Permission permission, Key key) {

        // Debo obtener el arreglo de dias de la semana en base al string de dias en permiso
        List<String> permissionDays = arrayManagementTools.convertStringToList(permission.getDays());

        // VALIDAR SI EL DIA DE LA KEY ESTA DENTRO DE LOS DIAS DEL PERMISO
        if (permissionDays.contains(key.getGenerationDay())) {

            return true;
        }

        return false;
    }

    @Override
    public Permission findPermissionByKeyId(Key keyId) {
        return permissionRepository.findByKeyIdAndActiveIsTrue(keyId).orElse(null);
    }

    @Override
    public List<Permission> getPermissionsByUser(User userId) {
        // para visitantes
        return permissionRepository.findAllByUserIdAndActiveIsTrueAndValidIsTrueAndStatusIsTrue(userId);
    }

}
