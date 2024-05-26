package com.example.neighsecureapi.repositories;

import com.example.neighsecureapi.domain.entities.Home;
import com.example.neighsecureapi.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HomeRepository extends JpaRepository<Home, UUID> {
    Optional<List<Home>> findAllByStatusIsTrue();
    Optional<Home> findByStatusIsTrueAndId(UUID id);
    Optional<Home> findByAddressAndHomeNumber(String address, Integer homeNumber);
    Optional<Home> findByStatusIsTrueAndHomeMemberIdOrHomeMemberId(User homeOwnerId, User homeMemberId);
    Optional<Home> findByHomeOwnerIdOrHomeMemberIdAndStatusIsTrue(User homeOwnerId, User homeMemberId);
}
