package com.hms.meenakshi.service;

import com.hms.meenakshi.entity.Room;
import com.hms.meenakshi.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private  final RoomRepository roomRepository;

    public void saveRoom(Room room){
        roomRepository.save(room);
    }

    public List<Room> getAvailableRooms() {
        List<Room> availableRooms = roomRepository.findAll().stream()
                .filter(r -> r.getOccupiedCount() < r.getCapacity())
                .collect(Collectors.toList());
        return availableRooms;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

}
