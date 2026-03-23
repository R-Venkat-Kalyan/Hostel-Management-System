package com.hms.meenakshi.service;

import com.hms.meenakshi.entity.FeeSummary;
import com.hms.meenakshi.entity.Room;
import com.hms.meenakshi.entity.RoomAssignment;
import com.hms.meenakshi.entity.User;
import com.hms.meenakshi.repository.FeeSummaryRepository;
import com.hms.meenakshi.repository.RoomAssignmentRepository;
import com.hms.meenakshi.repository.RoomRepository;
import com.hms.meenakshi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RoomAssignmentService {

    private final RoomAssignmentRepository assignmentRepository;
    private  final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private  final FeeSummaryRepository feeSummaryRepository;

    public void assignRoomToResident(String userId, String roomId) {
        // 1. Validate Room Availability
        Room room = roomRepository.findById(roomId).orElseThrow();
        if (room.getOccupiedCount() >= room.getCapacity()) {
            throw new IllegalStateException("Room is already full!");
        }

        // 2. Create the Assignment Record (The History)
        RoomAssignment assignment = new RoomAssignment();
        assignment.setUserId(userId);
        assignment.setRoomId(roomId);
        assignment.setStartDate(LocalDate.now());
        assignment.setStatus("ACTIVE");
        assignment.setCreatedAt(LocalDate.now());
        assignmentRepository.save(assignment);

        // 3. Update the Resident Entity (The Current State)
        User resident = userRepository.findById(userId).orElseThrow();
        resident.setRoomId(roomId);

        // 4. Create the Initial Fee Summary
        FeeSummary fee = new FeeSummary();
        fee.setUserId(userId);
        fee.setTotalAmount(room.getRentAmount()); // First month's rent
        fee.setPendingAmount(room.getRentAmount());
        fee.setStatus("UNPAID");
        feeSummaryRepository.save(fee);

        resident.setFeeSummaryId(fee.getId());
        userRepository.save(resident);

        // 5. Update the Room Occupancy
        room.setOccupiedCount(room.getOccupiedCount() + 1);
        if (room.getOccupiedCount().equals(room.getCapacity())) {
            room.setStatus("FULL");
        }
        roomRepository.save(room);
    }

}
