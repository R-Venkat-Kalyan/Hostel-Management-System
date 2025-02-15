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
    
    public Room getRoomByStuId(String stu_id) {
    	for(Room room: roomRepository.findAll()) {
    		if(room.getStudent().getStu_id().equals(stu_id))
    			return room;
    	}
    	return null;
    }
    
}

