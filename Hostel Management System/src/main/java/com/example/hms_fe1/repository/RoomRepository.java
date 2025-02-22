package com.example.hms_fe1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.hms_fe1.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
	
	@Query("SELECT r FROM Room r ORDER BY r.room_number ASC")
    List<Room> findAllSortedByRoomNumber();

}
