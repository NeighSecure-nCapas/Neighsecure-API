package com.example.neighsecureapi.services.serviceImpl;

import com.example.neighsecureapi.domain.dtos.HomeRegisterDTO;
import com.example.neighsecureapi.domain.entities.Home;
import com.example.neighsecureapi.domain.entities.User;
import com.example.neighsecureapi.repositories.HomeRepository;
import com.example.neighsecureapi.services.HomeService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class HomeServiceImplementation implements HomeService {

    private final HomeRepository homeRepository;

    public HomeServiceImplementation(HomeRepository homeRepository) {
        this.homeRepository = homeRepository;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void saveHome(HomeRegisterDTO info) {
        Home home = new Home();

        home.setHomeNumber(info.getHomeNumber());
        home.setAddress(info.getAddress());
        home.setHomeOwnerId(info.getUserAdmin());
        home.setStatus(true);
        home.setHomeMemberId(info.getHomeMembers());
        home.setMembersNumber(info.getMembersNumber());

        homeRepository.save(home);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void deleteHome(Home home) {

        home.setStatus(false);
        homeRepository.save(home);

    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void updateHome(Home home, HomeRegisterDTO info) {

        home.setHomeNumber(info.getHomeNumber());
        home.setAddress(info.getAddress());
        home.setMembersNumber(info.getMembersNumber());

        home.setHomeOwnerId(info.getUserAdmin());
        home.setHomeMemberId(info.getHomeMembers());

        //homeRepository.save(home);

    }

    @Override
    public Home getHome(UUID homeId) {
        return homeRepository.findByStatusIsTrueAndId(homeId).orElse(null);
    }

    @Override
    public List<Home> getAllHomes() {
        // TODO: implementar paginacion
        return homeRepository.findAllByStatusIsTrue().orElse(null);
    }

    @Override
    public Home findHomeByUser(User user) {
        return homeRepository.findByHomeOwnerIdOrHomeMemberIdAndStatusIsTrue(user, user).orElse(null);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void addHomeMembers(Home home, User homeMember) {

        // agregar los miembros a la lista de miembros
        List<User> currentMembers = home.getHomeMemberId();

        if(currentMembers == null) currentMembers = new ArrayList<>();

        // ya debe estar validado que cabe el nuevo miembro
        currentMembers.add(homeMember);

        homeRepository.save(home);

    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void removeHomeMembers(Home home, User homeMemberDel) {

        // Debe estar validado que el miembro a eliminar exista y sea parte de la lista
        home.getHomeMemberId().remove(homeMemberDel);

        homeRepository.save(home);

    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void updateHomeAdmin(Home home, User homeAdmin) {

        home.setHomeOwnerId(homeAdmin);
        homeRepository.save(home);

    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void removeHomeAdmin(String homeName, String homeAddress, String homeAdmins) {

    }

    @Override
    public Home findHomeByAddressAndHomeNumber(String address, Integer homeNumber) {
        return homeRepository.findByAddressAndHomeNumber(address, homeNumber).orElse(null);
    }

    @Override
    public boolean validateHomeMembersCapacity(Home home) {

        /*
        * // capacidad maxima de miembros de la casa, el admin no cuenta
        int capacity = home.getMembersNumber();

        // cantidad de miembros actuales
        int currentMembers = home.getHomeMemberId().size();

        // la cantidad actual de miembros mas el nuevo miembro no debe ser mayor a la capacidad
        return (currentMembers + 1) <= capacity;
        * */

        return (home.getHomeMemberId().size() + 1) <= home.getMembersNumber();
    }

}
