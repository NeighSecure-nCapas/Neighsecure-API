package com.example.neighsecureapi.services;

import com.example.neighsecureapi.domain.dtos.HomeRegisterDTO;
import com.example.neighsecureapi.domain.entities.Home;
import com.example.neighsecureapi.domain.entities.User;

import java.util.List;
import java.util.UUID;

public interface HomeService {

    // CRUD IMPLEMENTATION FOR HOME ENTITY -----------------------------------------------------------
    public void saveHome(HomeRegisterDTO info);
    public void deleteHome(Home homeId);
    public void updateHome(Home home, HomeRegisterDTO info);
    public Home getHome(UUID homeId);

    // TODO: implementar pagination
    public List<Home> getAllHomes();

    // END OF CRUD IMPLEMENTATION ---------------------------------------------------------------------

    // ADDITIONAL METHODS ----------------------------------------------------------------------------

    // METHODS FOR HOME MEMBERS -----------------------------------------------------------------------
    public void addHomeMembers(Home home, User homeMember);
    public void removeHomeMembers(Home home, User homeMemberDel);

    // METHODS FOR HOME ADMINS ------------------------------------------------------------------------
    public void updateHomeAdmin(Home home, User homeAdmin);
    public void removeHomeAdmin(String homeName, String homeAddress, String homeAdmins);
    public Home findHomeByAddressAndHomeNumber(String address, Integer homeNumber);

    // METHODS FOR HOME MEMBERS VALIDATION ------------------------------------------------------------------------
    public boolean validateHomeMembersCapacity(Home home);

}
