package com.example.neighsecureapi.services;

import com.example.neighsecureapi.domain.entities.Home;

import java.util.List;

public interface HomeService {

    // CRUD IMPLEMENTATION FOR HOME ENTITY
    // TODO: implementar dtos
    public void saveHome(String homeName, String homeAddress);
    public void deleteHome(String homeId);
    public void updateHome(String homeName, String homeAddress);
    public Home getHome(String homeName);

    // TODO: implementar pagination
    public List<Home> getAllHomes();

    // END OF CRUD IMPLEMENTATION ---------------------------------------------------------------------

    // ADDITIONAL METHODS ----------------------------------------------------------------------------

    // METHODS FOR HOME MEMBERS -----------------------------------------------------------------------
    public void addHomeMembers(String homeName, String homeAddress, String homeMembers);
    public void removeHomeMembers(String homeName, String homeAddress, String homeMembers);

    // METHODS FOR HOME ADMINS ------------------------------------------------------------------------
    public void addHomeAdmin(String homeName, String homeAddress, String homeAdmins);
    public void removeHomeAdmin(String homeName, String homeAddress, String homeAdmins);
    // TODO: validar si es necesaria la funcionalidad de actualizar el admin
    public void updateHomeAdmin(String homeName, String homeAddress, String homeAdmins);

}
