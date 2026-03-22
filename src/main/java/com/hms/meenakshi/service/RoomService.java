package com.hms.meenakshi.service;

import com.hms.meenakshi.entity.Room;
import com.hms.meenakshi.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

    private  final RoomRepository roomRepository;

    public void saveRoom(Room room){
        roomRepository.save(room);
    }

}
