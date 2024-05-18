package com.example.neighsecureapi.services;

import com.example.neighsecureapi.domain.dtos.HomeRegisterDTO;
import com.example.neighsecureapi.domain.entities.Home;
import com.example.neighsecureapi.domain.entities.User;

import java.util.List;
import java.util.UUID;

public interface HomeService {

    // CRUD IMPLEMENTATION FOR HOME ENTITY -----------------------------------------------------------
    public void saveHome(HomeRegisterDTO info, User userAdmin, List<User> homeMembers);
    public void deleteHome(UUID homeId);
    public void updateHome(UUID homeid, HomeRegisterDTO info);
    public Home getHome(UUID homeId);

    // TODO: implementar pagination
    public List<Home> getAllHomes();

    // END OF CRUD IMPLEMENTATION ---------------------------------------------------------------------

    // ADDITIONAL METHODS ----------------------------------------------------------------------------

    // METHODS FOR HOME MEMBERS -----------------------------------------------------------------------
    public void addHomeMembers(UUID homeId, User homeMember);
    public void removeHomeMembers(UUID homeId, User homeMemberDel);

    // METHODS FOR HOME ADMINS ------------------------------------------------------------------------
    public void updateHomeAdmin(UUID homeId, User homeAdmin);
    public void removeHomeAdmin(String homeName, String homeAddress, String homeAdmins);

    // METHODS FOR HOME MEMBERS VALIDATION ------------------------------------------------------------------------
    public boolean validateHomeMembersCapacity(UUID homeId);

}
