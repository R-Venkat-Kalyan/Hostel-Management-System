package com.hms.meenakshi.repository;

import com.hms.meenakshi.entity.RoomAssignment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomAssignmentRepository extends MongoRepository<RoomAssignment, String> {
}
