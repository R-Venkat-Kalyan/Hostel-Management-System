package com.hms.meenakshi.repository;

import com.hms.meenakshi.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

        List<User> findByRoleAndRoomId(String role, String roomId);

}
