package com.cntt.rentalmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cntt.rentalmanagement.domain.enums.RoomStatus;
import com.cntt.rentalmanagement.domain.models.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByCode(String code);
    List<Room> findByStatus(RoomStatus status);
    long countByRoomTypeIdAndStatus(Long roomTypeId, RoomStatus status);
}
