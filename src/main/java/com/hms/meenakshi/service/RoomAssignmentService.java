package com.hms.meenakshi.service;

import com.hms.meenakshi.entity.*;
import com.hms.meenakshi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RoomAssignmentService {

    private final RoomAssignmentRepository assignmentRepository;
    private  final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private  final FeeSummaryRepository feeSummaryRepository;
    private final VacatedResidentRepository vacatedRepo;
    private final AuthService authService;
    private final UserReceiptsService userReceiptsService;

    @Transactional
    public void assignRoomToResident(String userId, String roomId) {
        // 1. Validate Room Availability
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (room.getOccupiedCount() >= room.getCapacity()) {
            throw new IllegalStateException("Room is already full!");
        }

        // 2. Create the Assignment Record (The History)
        RoomAssignment assignment = new RoomAssignment();
        assignment.setUserId(userId);
        assignment.setRoomId(roomId);
        assignment.setStartDate(LocalDate.now());
        assignment.setEndDate(LocalDate.now().plusYears(4));
        assignment.setStatus("ACTIVE");
//        assignment.setCreatedAt(LocalDate.now());
        assignmentRepository.save(assignment);

        // 3. Update the Resident Entity
        User resident = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Resident not found"));
        resident.setRoomId(roomId);

        // 4. Create the Initial Fee Summary
        FeeSummary fee = new FeeSummary();
        fee.setUserId(userId);
        fee.setTotalAmount(room.getRentAmount());
        fee.setPaidAmount(0.0);
        fee.setPendingAmount(room.getRentAmount());
        fee.setStatus("UNPAID");
        fee.setLastPaymentDate(null);
        fee.setNextDueDate(LocalDate.now().plusDays(2));
        feeSummaryRepository.save(fee);

        resident.setFeeSummaryId(fee.getId());
        userRepository.save(resident);

        // 5. Update the Room Occupancy
        room.setOccupiedCount(room.getOccupiedCount() + 1);
        if (room.getOccupiedCount() >= room.getCapacity()) {
            room.setStatus("FULL");
        } else {
            room.setStatus("AVAILABLE");
        }
        roomRepository.save(room);
    }

    @Transactional
    public void handleResidentExit(String userId, String reason, boolean isSwitch) {
        // 1. Fetch User and current data for archiving
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // We use a custom query or your existing snapshot logic to get the room number
        Room room = roomRepository.findById(user.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room mapping not found"));

        FeeSummary fee = feeSummaryRepository.findByUserId(userId);

        // 2. Hard Check: Dues must be zero for permanent exit
        if (!isSwitch && fee != null && fee.getPendingAmount() > 0) {
            throw new IllegalStateException("Clear dues (₹" + fee.getPendingAmount() + ") before vacating.");
        }

        // 3. Create Archive Entry
        VacatedResident archive = new VacatedResident();
        archive.setName(user.getFullName());
        archive.setUniversityId(user.getUniversityId());
        archive.setRoomNumber(room.getRoomNumber());
        archive.setJoinedDate(user.getCreatedAt());
        archive.setVacatedDate(LocalDate.now());
        archive.setTotalPaidDuringStay(fee != null ? fee.getPaidAmount() : 0.0);
        archive.setReason(isSwitch ? "ROOM_SWITCH" : "PERMANENT_EXIT");
        vacatedRepo.save(archive);

        // 4. Update Room Capacity (Old Room)
        room.setOccupiedCount(Math.max(0, room.getOccupiedCount() - 1));
        room.setStatus("AVAILABLE");
        roomRepository.save(room);

        // 5. Cleanup Assignments and Fees
        assignmentRepository.deleteByUserId(userId);
        feeSummaryRepository.deleteByUserId(userId);

        userReceiptsService.deleteAllReceiptsByUserId(userId);

        // 6. Branching Logic: Switch vs Vacate
        if (isSwitch) {
            // Reset User to 'NEW' so they appear in the 'Assign Room' list
            user.setRoomId("NEW");
            user.setFeeSummaryId("NEW");
            userRepository.save(user);
        } else {
            // Permanent Delete from active users
            userRepository.deleteById(userId);
        }

        // 7. Async Email to Owner & Manager
        authService.sendVacateNoticeAsync(archive, user.getEmail());
    }

    }

