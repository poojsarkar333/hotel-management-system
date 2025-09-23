package com.hotel.room_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;

import com.hotel.room_service.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @NativeQuery("SELECT * FROM tbl_room WHERE room_number = :roomNumber")
	Room findByRoomNumber(String roomNumber);
}
