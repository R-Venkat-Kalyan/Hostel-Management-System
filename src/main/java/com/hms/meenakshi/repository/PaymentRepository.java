package com.hms.meenakshi.repository;

import com.hms.meenakshi.entity.Payments;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<Payments, String> {

    List<Payments> findByUserIdOrderByPaidDateDesc(String userId);
}
