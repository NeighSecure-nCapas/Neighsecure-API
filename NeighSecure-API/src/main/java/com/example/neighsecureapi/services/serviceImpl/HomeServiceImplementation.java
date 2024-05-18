package com.example.neighsecureapi.services.serviceImpl;

import com.example.neighsecureapi.domain.dtos.HomeRegisterDTO;
import com.example.neighsecureapi.domain.entities.Home;
import com.example.neighsecureapi.domain.entities.User;
import com.example.neighsecureapi.repositories.HomeRepository;
import com.example.neighsecureapi.services.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class HomeServiceImplementation implements HomeService {

    private final HomeRepository homeRepository;

    public HomeServiceImplementation(HomeRepository homeRepository) {
        this.homeRepository = homeRepository;
    }

    @Override
    public void saveHome(HomeRegisterDTO info, User userAdmin, List<User> homeMembers) {
        Home home = new Home();

        home.setHomeNumber(info.getHomeNumber());
        home.setAddress(info.getAddress());
        home.setHomeOwnerId(userAdmin);
        home.setStatus(true);
        home.setHomeMemberId(homeMembers);
        home.setMembersNumber(info.getMembersNumber());

        homeRepository.save(home);
    }

    @Override
    public void deleteHome(UUID homeId) {
        Home home = homeRepository.findById(homeId).orElse(null);

        if (home != null) {
            home.setStatus(false);
            homeRepository.save(home);
        }
    }

    @Override
    public void updateHome(Home home, HomeRegisterDTO info) {

        if(info.getHomeNumber() != null) home.setHomeNumber(info.getHomeNumber());
        if(info.getAddress() != null) home.setAddress(info.getAddress());
        if(info.getMembersNumber() != null) home.setMembersNumber(info.getMembersNumber());

        homeRepository.save(home);

    }

    @Override
    public Home getHome(UUID homeId) {
        return homeRepository.findById(homeId).orElse(null);
    }

    @Override
    public List<Home> getAllHomes() {
        // TODO: implementar paginacion
        return homeRepository.findAllByStatusIsTrue();
    }

    @Override
    public void addHomeMembers(Home home, User homeMember) {

        // agregar los miembros a la lista de miembros
        List<User> currentMembers = home.getHomeMemberId();

        // ya debe estar validado que cabe el nuevo miembro
        currentMembers.add(homeMember);

        homeRepository.save(home);

    }

    @Override
    public void removeHomeMembers(Home home, User homeMemberDel) {

        // Debe estar validado que el miembro a eliminar exista y sea parte de la lista
        home.getHomeMemberId().remove(homeMemberDel);

        homeRepository.save(home);

    }

    @Override
    public void updateHomeAdmin(Home home, User homeAdmin) {

        home.setHomeOwnerId(homeAdmin);
        homeRepository.save(home);

    }

    @Override
    public void removeHomeAdmin(String homeName, String homeAddress, String homeAdmins) {

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
