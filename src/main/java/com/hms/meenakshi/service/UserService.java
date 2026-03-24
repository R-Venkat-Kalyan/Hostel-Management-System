package com.hms.meenakshi.service;

import com.hms.meenakshi.dto.ResidentDetailsDTO;
import com.hms.meenakshi.entity.User;
import com.hms.meenakshi.repository.FeeSummaryRepository;
import com.hms.meenakshi.repository.RoomRepository;
import com.hms.meenakshi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private  final UserRepository userRepository;
    private  final RoomRepository roomRepository;
    private  final FeeSummaryRepository feeSummaryRepository;

    public void saveUser(User user){
        userRepository.save(user);
    }

    public List<User> findByRoleAndRoomId(String role, String roomId) {
        return userRepository.findByRoleAndRoomId(role, roomId);
    }

    public List<ResidentDetailsDTO> getAllResidentSnapshots() {
        List<User> residents = userRepository.findByRoleAndRoomIdNot("RESIDENT", "NEW");

        return residents.stream().map(user -> {
            ResidentDetailsDTO dto = new ResidentDetailsDTO();
            dto.setUserId(user.getId());
            dto.setName(user.getFullName());
            dto.setUniversityId(user.getUniversityId());

            roomRepository.findById(user.getRoomId()).ifPresent(r -> {
                dto.setRoom(r.getRoomNumber());
                dto.setRoomType(r.getRoomType());
            });

            feeSummaryRepository.findById(user.getFeeSummaryId()).ifPresent(f -> {
                dto.setTotalPayment(f.getTotalAmount());
                dto.setDuePayment(f.getPendingAmount());
                dto.setLastPaidDate(f.getLastPaymentDate());
            });

            return dto;
        }).collect(Collectors.toList());
    }
}
