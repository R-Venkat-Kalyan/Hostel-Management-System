package com.example.hms_fe1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.hms_fe1.entity.UserEntity;
import com.example.hms_fe1.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	public void saveUser(UserEntity user) {
		userRepository.save(user);
	}
	
	
	

}
