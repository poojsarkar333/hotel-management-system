package com.hotel.room_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.room_service.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
