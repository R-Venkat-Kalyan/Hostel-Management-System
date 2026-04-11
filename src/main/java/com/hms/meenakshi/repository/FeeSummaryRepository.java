package com.hms.meenakshi.repository;

import com.hms.meenakshi.entity.FeeSummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeeSummaryRepository extends MongoRepository<FeeSummary, String> {
    
    void deleteByUserId(String userId);

    FeeSummary findByUserId(String userId);
}
