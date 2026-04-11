package com.hms.meenakshi.repository;

import com.hms.meenakshi.entity.UserReceipts;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserReceiptsRepository extends MongoRepository<UserReceipts, String> {

    List<UserReceipts> findByUserIdOrderByPaymentDateDesc(String userId);

    void deleteByUserId(String userId);
}
