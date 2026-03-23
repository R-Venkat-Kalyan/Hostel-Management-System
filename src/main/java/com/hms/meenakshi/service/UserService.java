package com.hms.meenakshi.service;

import com.hms.meenakshi.entity.User;
import com.hms.meenakshi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private  final UserRepository userRepository;

    public void saveUser(User user){
        userRepository.save(user);
    }

    public List<User> findByRoleAndRoomId(String role, String roomId) {
        return userRepository.findByRoleAndRoomId(role, roomId);
    }
}
