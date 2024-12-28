package com.example.hms_fe1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hms_fe1.entity.Room;
import com.example.hms_fe1.entity.Student;
import com.example.hms_fe1.repository.RoomRepository;

@Service
public class RoomService {
	
    @Autowired
    private RoomRepository roomRepository;

    public void save(String roomId, Student student) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> 
            new IllegalArgumentException("Room not found!"));

        // Assign the student to the room
        room.setStudent(student);

        // Save the updated room
        roomRepository.save(room);
    }
}

