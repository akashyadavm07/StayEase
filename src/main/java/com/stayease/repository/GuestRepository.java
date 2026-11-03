
package com.stayease.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stayease.model.Guest;

public interface GuestRepository extends JpaRepository<Guest, Long> {

    Optional<Guest> findByRoomNumber(String roomNumber);

}
