package com.cntt.rentalmanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cntt.rentalmanagement.domain.models.RoomType;

public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {
    Optional<RoomType> findByNameIgnoreCase(String name);
}
