package com.hms.meenakshi.repository;

import com.hms.meenakshi.entity.FeedBack;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedBackRepository extends MongoRepository<FeedBack, String> {
}
